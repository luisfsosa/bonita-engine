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
package org.bonitasoft.engine.api.impl.transaction.expression;

import java.io.Serializable;
import java.util.Map;

import org.bonitasoft.engine.expression.Expression;

/**
 * @author Emmanuel Duchastenier
 */
public abstract class AbstractEvaluateExpressionsInstance {

    protected String buildName(final Expression exp) {
        String value = null;
        if (exp != null) {
            value = exp.getName() != null ? exp.getName() : exp.getContent();
        }
        return value;
    }

    protected Map<String, Serializable> getPartialContext(final Map<Expression, Map<String, Serializable>> expressions, final Expression exp) {
        return expressions.get(exp);
    }
}
