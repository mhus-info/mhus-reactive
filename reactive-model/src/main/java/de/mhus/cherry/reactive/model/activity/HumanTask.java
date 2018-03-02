package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.lib.form.DataSource;

public interface HumanTask<P extends Pool> extends Task<P> {

	DataSource createDataSource();

	HumanForm createForm();

}
