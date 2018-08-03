package de.mhus.cherry.reactive.model.ui;

import de.mhus.lib.form.IFormInformation;

public interface IPool {

	String getDisplayName();

	String getDescription();

	IFormInformation getInitialForm();
	
	IFormInformation getDisplayForm();

}
