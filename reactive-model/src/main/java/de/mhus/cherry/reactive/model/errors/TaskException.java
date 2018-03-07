package de.mhus.cherry.reactive.model.errors;

import de.mhus.cherry.reactive.model.activity.Activity;

public class TaskException extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskException(String msg, Class<? extends Activity<?>> handler) {
		
	}
}
