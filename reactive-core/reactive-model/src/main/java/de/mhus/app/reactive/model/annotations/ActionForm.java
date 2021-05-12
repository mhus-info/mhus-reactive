package de.mhus.app.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.app.reactive.model.util.NoForm;
import de.mhus.lib.form.IFormInformation;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActionForm {

    Class<? extends IFormInformation> value() default NoForm.class;

}
