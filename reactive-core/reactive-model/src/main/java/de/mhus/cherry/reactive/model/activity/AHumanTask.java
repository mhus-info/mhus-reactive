package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.cherry.reactive.model.util.IndexValuesProvider;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.errors.MException;

public interface AHumanTask<P extends APool<?>> extends ATask<P>, IndexValuesProvider {

	HumanForm createForm();
	
	IProperties getFormValues() throws MException;
	
	void doSubmit(IProperties values);

	
}
