package de.mhus.cherry.reactive.model.activity;

public abstract class Task extends Activity {

	public abstract Class<? extends Activity> doExecute() throws Exception;
}
