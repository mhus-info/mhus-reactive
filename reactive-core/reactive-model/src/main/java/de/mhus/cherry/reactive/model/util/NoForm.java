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
