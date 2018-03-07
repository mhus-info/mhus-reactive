package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.lib.core.IProperties;

public interface HumanTask<P extends Pool<?>> extends Task<P> {

	IProperties getFormValues();
	
	void setFormValues(IProperties values);

	HumanForm createForm();

}
