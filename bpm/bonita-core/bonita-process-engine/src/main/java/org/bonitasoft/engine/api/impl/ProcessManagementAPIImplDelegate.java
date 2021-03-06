/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.api.impl;

import java.io.File;
import java.io.IOException;

import org.bonitasoft.engine.api.impl.transaction.process.DeleteProcess;
import org.bonitasoft.engine.api.impl.transaction.process.DisableProcess;
import org.bonitasoft.engine.bpm.process.ActivationState;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.core.process.definition.ProcessDefinitionService;
import org.bonitasoft.engine.core.process.definition.exception.SProcessDefinitionNotFoundException;
import org.bonitasoft.engine.core.process.definition.exception.SProcessDefinitionReadException;
import org.bonitasoft.engine.core.process.definition.model.SProcessDefinitionDeployInfo;
import org.bonitasoft.engine.core.process.instance.api.ProcessInstanceService;
import org.bonitasoft.engine.core.process.instance.api.event.EventInstanceService;
import org.bonitasoft.engine.dependency.model.ScopeType;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.RetrieveException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.home.BonitaHomeServer;
import org.bonitasoft.engine.io.IOUtil;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.bonitasoft.engine.persistence.SBonitaReadException;
import org.bonitasoft.engine.scheduler.SchedulerService;
import org.bonitasoft.engine.service.PlatformServiceAccessor;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.service.TenantServiceSingleton;
import org.bonitasoft.engine.service.impl.ServiceAccessorFactory;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;

/**
 * @author Matthieu Chaffotte
 */
// Uncomment the "implements" when this delegate implements all the methods.
public class ProcessManagementAPIImplDelegate /* implements ProcessManagementAPI */{

    protected TenantServiceAccessor getTenantAccessor() {
        try {
            final SessionAccessor sessionAccessor = ServiceAccessorFactory.getInstance().createSessionAccessor();
            final long tenantId = sessionAccessor.getTenantId();
            return TenantServiceSingleton.getInstance(tenantId);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PlatformServiceAccessor getPlatformServiceAccessor() {
        try {
            return ServiceAccessorFactory.getInstance().createPlatformServiceAccessor();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProcessDefinition(final long processDefinitionId) throws SBonitaException, BonitaHomeNotSetException, IOException {
        final TenantServiceAccessor tenantAccessor = getTenantAccessor();
        final TechnicalLoggerService logger = tenantAccessor.getTechnicalLoggerService();

        final DeleteProcess deleteProcess = instantiateDeleteProcessTransactionContent(processDefinitionId);
        deleteProcess.execute();

        final String processesFolder = BonitaHomeServer.getInstance().getProcessesFolder(tenantAccessor.getTenantId());
        final File file = new File(processesFolder);
        if (!file.exists()) {
            file.mkdir();
        }

        final File processFolder = new File(file, String.valueOf(processDefinitionId));
        IOUtil.deleteDir(processFolder);
        if (logger.isLoggable(getClass(), TechnicalLogSeverity.INFO)) {
            logger.log(this.getClass(), TechnicalLogSeverity.INFO, "The user <" + SessionInfos.getUserNameFromSession() + "> has deleted process with id = <"
                    + processDefinitionId + ">");
        }
    }

    @Deprecated
    public void deleteProcess(final long processDefinitionId) throws SBonitaException, BonitaHomeNotSetException {
        final TenantServiceAccessor tenantAccessor = getTenantAccessor();
        final DeleteProcess deleteProcess = new DeleteProcess(getTenantAccessor(), processDefinitionId);
        deleteProcess.execute();

        final String processesFolder = BonitaHomeServer.getInstance().getProcessesFolder(tenantAccessor.getTenantId());
        final File file = new File(processesFolder);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    protected DeleteProcess instantiateDeleteProcessTransactionContent(final long processId) {
        return new DeleteProcess(getTenantAccessor(), processId);
    }

    public void disableProcess(final long processId) throws SProcessDefinitionNotFoundException, SBonitaException {
        final TenantServiceAccessor tenantAccessor = getTenantAccessor();
        final PlatformServiceAccessor platformServiceAccessor = getPlatformServiceAccessor();
        final ProcessDefinitionService processDefinitionService = tenantAccessor.getProcessDefinitionService();
        final EventInstanceService eventInstanceService = tenantAccessor.getEventInstanceService();
        final SchedulerService schedulerService = platformServiceAccessor.getSchedulerService();
        final TechnicalLoggerService logger = tenantAccessor.getTechnicalLoggerService();
        final DisableProcess disableProcess = new DisableProcess(processDefinitionService, processId, eventInstanceService, schedulerService, logger,
                SessionInfos.getUserNameFromSession());
        disableProcess.execute();
    }

    public void purgeClassLoader(final long processDefinitionId) throws ProcessDefinitionNotFoundException, UpdateException {
        final TenantServiceAccessor tenantAccessor = getTenantAccessor();
        final ProcessDefinitionService processDefinitionService = tenantAccessor.getProcessDefinitionService();
        try {
            final SProcessDefinitionDeployInfo processDeploymentInfo = processDefinitionService.getProcessDeploymentInfo(processDefinitionId);
            if (!ActivationState.DISABLED.name().equals(processDeploymentInfo.getActivationState())) {
                throw new UpdateException("Purge can only be done on a disabled process");
            }
            final ProcessInstanceService processInstanceService = tenantAccessor.getProcessInstanceService();
            final long numberOfProcessInstances = processInstanceService.getNumberOfProcessInstances(processDefinitionId);
            if (numberOfProcessInstances != 0) {
                throw new UpdateException("Purge can only be done on a disabled process with no running instances");
            }
            tenantAccessor.getClassLoaderService().removeLocalClassLoader(ScopeType.PROCESS.name(), processDefinitionId);
        } catch (final SProcessDefinitionNotFoundException spdnfe) {
            throw new ProcessDefinitionNotFoundException(spdnfe);
        } catch (final SProcessDefinitionReadException spdre) {
            throw new RetrieveException(spdre);
        } catch (final SBonitaReadException sbre) {
            throw new RetrieveException(sbre);
        }
    }

}
