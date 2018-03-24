package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.cherry.reactive.model.util.IndexValuesProvider;
import de.mhus.lib.core.IProperties;

public interface AHumanTask<P extends APool<?>> extends ATask<P>, IndexValuesProvider {

	IProperties getFormValues();
	
	void setFormValues(IProperties values);

	HumanForm createForm();
	
}
