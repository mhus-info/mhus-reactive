package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

public class ReactiveFlow {

	public enum STATE {NEW, READY,SCHEDULED,FAILED,SUSPENDED,STOPPED,CLOSED};
	private UUID id;
	private UUID caseId;
	private String name;
	private long creationDate;
	private STATE state = STATE.NEW;
	private long scheduled;
	private long timerTrigger;
	private String signalTrigger;
	private String messageTrigger;
	// if set to true the process will not switch to the next activities and enter STOPPED state
	private boolean stopAfterExecute;
	private UUID previousActivity;
	private String[] nextActivity;
	// Internal message to describe the exit reason, e.g. exception, event called ...
	private String exitMessage;
	
	
	
}
