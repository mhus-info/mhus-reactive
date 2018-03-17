package de.mhus.cherry.reactive.osgi.impl;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MLog;

@Component(immediate=true)
public class ReactiveAdminImpl extends MLog implements ReactiveAdmin {

	private ReactiveAdminImpl instance;

	@Activate
	public void doActivate(ComponentContext ctx) {
		instance = this;
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		instance = null;
	}

}
