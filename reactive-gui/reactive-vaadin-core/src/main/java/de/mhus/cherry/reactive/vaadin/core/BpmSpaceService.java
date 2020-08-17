/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
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
package de.mhus.cherry.reactive.vaadin.core;

import java.util.Locale;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.MenuBar.MenuItem;

import org.osgi.service.component.annotations.Component;
import de.mhus.lib.vaadin.desktop.SimpleGuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;
import de.mhus.lib.vaadin.desktop.HelpContext;

@Component(immediate = true, service = GuiSpaceService.class)
public class BpmSpaceService extends SimpleGuiSpace {

    @Override
    public String getName() {
        return "bpm";
    }

    @Override
    public String getDisplayName(Locale locale) {
        return "BPM";
    }

    @Override
    public AbstractComponent createSpace() {
        return new BpmSpace(this);
    }

    @Override
    public HelpContext createHelpContext(Locale locale) {
        return null;
    }

    @Override
    public void createMenu(AbstractComponent space, MenuItem[] menu) {
        ((BpmSpace) space).createMenu(menu);
    }
}
