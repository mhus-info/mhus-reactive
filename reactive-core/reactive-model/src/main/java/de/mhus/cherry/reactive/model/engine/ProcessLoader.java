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
package de.mhus.cherry.reactive.model.engine;

import java.util.List;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;

public interface ProcessLoader {

    /**
     * Return all found classes that implements AElement.
     *
     * @return all elements
     */
    List<Class<? extends AElement<?>>> getElements();

    /**
     * Return the process canonical name "class name:version" or null if not possible.
     *
     * @return The process canonical name
     */
    default String getProcessCanonicalName() {
        try {
            for (Class<? extends AElement<?>> clazz : getElements()) {
                if (AProcess.class.isAssignableFrom(clazz)) {
                    // EngineUtil
                    ProcessDescription desc = clazz.getAnnotation(ProcessDescription.class);
                    if (desc != null) {
                        return clazz.getCanonicalName() + ":" + desc.version();
                    }
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }
}
