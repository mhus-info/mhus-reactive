/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.engine.ProcessContext;

/**
 * Implements a condition for gateways.
 *
 * @author mikehummel
 * @param <P>
 */
public interface ACondition<P extends APool<?>> extends AElement<P> {

    static final int TRUE = 1;
    static final int FALSE = -1;

    /**
     * Return true or false or for complex conditions the highest value will win.
     *
     * @param context
     * @return the result of the check
     */
    int check(ProcessContext<P> context);
}
