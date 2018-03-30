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
