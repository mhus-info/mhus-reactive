package de.mhus.cherry.reactive.model.activity;

public interface Task<P extends Pool> extends Activity<P> {

	Class<? extends Activity<P>> doExecute() throws Exception;

}
