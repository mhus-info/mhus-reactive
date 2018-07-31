package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.UserForm;

/**
 * This interface is needed for classes providing forms.
 * 
 * @author mikehummel
 *
 */
public interface AFormProvider {

	UserForm createForm();

}
