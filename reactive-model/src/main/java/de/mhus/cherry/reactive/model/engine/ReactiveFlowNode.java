package de.mhus.cherry.reactive.model.engine;

import java.util.Map;
import java.util.UUID;

public class ReactiveFlowNode {

	public enum STATE {NEW, READY,SCHEDULED,WAITING,FAILED,SUSPENDED,STOPPED,CLOSED};
	protected UUID id;
	protected UUID caseId;
	protected String name;
	protected long creationDate;
	protected STATE state = STATE.NEW;
	protected long scheduled;
	protected long timerTrigger;
	protected String signalTrigger;
	protected String messageTrigger;
	// if set to true the process will not switch to the next activities and enter STOPPED state
	protected boolean stopAfterExecute;
	protected UUID previousActivity;
	protected String[] nextActivity;
	// Internal message to describe the exit reason, e.g. exception, event called ...
	protected String exitMessage;
	
	protected Map<String,Object> parameters;
	
	
	
}
