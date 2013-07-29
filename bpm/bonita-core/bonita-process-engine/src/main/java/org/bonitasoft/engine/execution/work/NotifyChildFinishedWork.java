/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.engine.execution.work;

import org.bonitasoft.engine.core.process.definition.model.SProcessDefinition;
import org.bonitasoft.engine.core.process.instance.api.states.FlowNodeState;
import org.bonitasoft.engine.core.process.instance.model.SFlowNodeInstance;
import org.bonitasoft.engine.execution.ContainerRegistry;
import org.bonitasoft.engine.work.TxBonitaWork;

/**
 * @author Baptiste Mesta
 */
public class NotifyChildFinishedWork extends TxBonitaWork {

    private final ContainerRegistry containerRegistry;

    private final SProcessDefinition processDefinition;

    private final SFlowNodeInstance flowNodeInstance;

    private final FlowNodeState state;

    public NotifyChildFinishedWork(ContainerRegistry containerRegistry, SProcessDefinition processDefinition, SFlowNodeInstance flowNodeInstance,
            FlowNodeState state) {
        super();
        this.containerRegistry = containerRegistry;
        this.processDefinition = processDefinition;
        this.flowNodeInstance = flowNodeInstance;
        this.state = state;
    }

    @Override
    protected void work() throws Exception {
        containerRegistry.nodeReachedState(processDefinition, flowNodeInstance, state, flowNodeInstance.getParentContainerId(), flowNodeInstance
                .getParentContainerType().name());

    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName() + ": processInstanceId:" + flowNodeInstance.getParentContainerId() + ", flowNodeInstanceId: "
                + flowNodeInstance.getId();
    }
}
