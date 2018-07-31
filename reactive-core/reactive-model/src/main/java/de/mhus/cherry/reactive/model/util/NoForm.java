package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AFormProvider;

public class NoForm implements AFormProvider {

	@Override
	public UserForm createForm() {
		return null;
	}

}
