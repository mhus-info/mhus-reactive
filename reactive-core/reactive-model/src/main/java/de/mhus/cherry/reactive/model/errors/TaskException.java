package de.mhus.cherry.reactive.model.errors;

public class TaskException extends Exception {

	private static final long serialVersionUID = 1L;
	private String trigger;

	public TaskException(String msg, String trigger) {
		super(trigger + ":" + msg);
		this.trigger = trigger;
	}

	public String getTrigger() {
		return trigger;
	}

}
