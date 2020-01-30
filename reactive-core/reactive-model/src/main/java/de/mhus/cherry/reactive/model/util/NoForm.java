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
package de.mhus.cherry.reactive.model.util;

import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;

public class NoForm implements IFormInformation {

    @Override
    public DefRoot getForm() {
        return null;
    }

    @Override
    public Class<? extends ActionHandler> getActionHandler() {
        return null;
    }

    @Override
    public Class<? extends FormControl> getFormControl() {
        return null;
    }
}
