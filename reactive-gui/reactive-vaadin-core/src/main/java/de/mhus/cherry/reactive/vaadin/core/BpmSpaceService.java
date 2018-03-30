/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.vaadin.core;

import java.util.Locale;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.vaadin.desktop.GuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;
import de.mhus.lib.vaadin.desktop.HelpContext;

@Component(immediate=true,provide=GuiSpaceService.class)
public class BpmSpaceService extends GuiSpace {

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

}
