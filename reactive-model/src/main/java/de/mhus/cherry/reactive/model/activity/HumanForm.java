package de.mhus.cherry.reactive.model.activity;

import de.mhus.lib.core.definition.DefComponent;
import de.mhus.lib.core.definition.DefRoot;

public class HumanForm {
	
	DefRoot root = new DefRoot();
	
	public HumanForm add(DefComponent ... components) {
		root.addDefinition(components);
		return this;
	}
	
}
