/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
package org.bonitasoft.engine.dependency.model.builder.impl;

import org.bonitasoft.engine.dependency.model.builder.SDependencyMappingLogBuilder;
import org.bonitasoft.engine.queriablelogger.model.SQueriableLog;
import org.bonitasoft.engine.queriablelogger.model.builder.SPersistenceLogBuilder;
import org.bonitasoft.engine.queriablelogger.model.builder.impl.CRUDELogBuilder;
import org.bonitasoft.engine.queriablelogger.model.builder.impl.MissingMandatoryFieldsException;

/**
 * @author Yanyan Liu
 */
public class SDependencyMappingLogBuilderImpl extends CRUDELogBuilder implements SDependencyMappingLogBuilder {

    private static final String PREFIX = "DEPENDENCY_MAPPING";

    @Override
    public SPersistenceLogBuilder objectId(final long objectId) {
        queriableLogBuilder.numericIndex(SDependencyLogIndexesMapper.DEPENDENCY_MAPPING_INDEX, objectId);
        return this;
    }

    @Override
    protected String getActionTypePrefix() {
        return PREFIX;
    }

    @Override
    protected void checkExtraRules(final SQueriableLog log) {
        if (log.getNumericIndex(SDependencyLogIndexesMapper.DEPENDENCY_INDEX) == 0L) {
            throw new MissingMandatoryFieldsException("Some mandatoryFildes are missing: " + "Dependency Id");
        }
        if (log.getActionStatus() != SQueriableLog.STATUS_FAIL) {
            if (log.getNumericIndex(SDependencyLogIndexesMapper.DEPENDENCY_MAPPING_INDEX) == 0L) {
                throw new MissingMandatoryFieldsException("Some mandatoryFildes are missing: " + "Dependency Mapping Id");
            }
        }
    }

    @Override
    public SDependencyMappingLogBuilder dependencyId(final long dependencyId) {
        queriableLogBuilder.numericIndex(SDependencyLogIndexesMapper.DEPENDENCY_INDEX, dependencyId);
        return this;
    }

}
