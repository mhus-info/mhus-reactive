/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.reactive.examples.simple1;

import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;
import de.mhus.lib.form.definition.FaShowInformationPanel;
import de.mhus.lib.form.definition.FmText;

public class S1PoolTestForm implements IFormInformation {

    @Override
    public DefRoot getForm() {
        return new DefRoot(
                new FaShowInformationPanel(),
                new FmText("name", "Name", "Insert the name"),
                new FmText("desc", "Description", "Insert description"));
    }

    @Override
    public Class<? extends ActionHandler> getActionHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends FormControl> getFormControl() {
        // TODO Auto-generated method stub
        return null;
    }
}
