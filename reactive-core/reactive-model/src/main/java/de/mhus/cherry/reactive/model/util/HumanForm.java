package de.mhus.cherry.reactive.model.util;

import de.mhus.lib.core.definition.DefComponent;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.errors.MException;

public class HumanForm {
	
	DefRoot root = new DefRoot();
	
	public HumanForm add(DefComponent ... components) {
		root.addDefinition(components);
		return this;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}

	public HumanForm build() throws MException {
		root.build();
		return this;
	}
	
	public DefRoot getRoot() {
		return root;
	}
	
}
