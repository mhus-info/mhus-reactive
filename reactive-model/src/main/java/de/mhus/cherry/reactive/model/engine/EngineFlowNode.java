package de.mhus.cherry.reactive.model.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EngineFlowNode {

	// NEW: before save to store
	// READY: ready to proceess
	// SCHEDULED: Waiting for the scheduleing time
	// WAITING: Waiting to be done, e.g. (Human Tasks, Messages, Signals)
	// FAILED: An error occured, Node is stopped
	// SUSPEDNED: Suspended by e.g. admin
	// STOPPED: Some kind of SUSPENDED
	// CLOSED: The task is done and can be archived with the case, needed for history
	
	public enum STATE {NEW, READY,SCHEDULED,WAITING,FAILED,SUSPENDED,STOPPED,CLOSED};
	// ident of this flow node
	protected UUID id;
	// ident of the case
	protected UUID caseId;
	// name of this low node (e.g. activity)
	protected String name;
	// created
	protected long creationDate;
	// last running time
	protected long lastRunDate;
	// state
	protected STATE state = STATE.NEW;
	// list of schedulers and the source, only the nearest scheduling time is needed to be triggered
	protected HashMap<String,Long> schedulers;
	// list of singnals to wait for
	protected HashMap<String,String> signalTriggers;
	// list of messages to wait for
	protected HashMap<String,String> messageTriggers;
	// if set to true the process will not switch to the next activities and enter STOPPED state
	protected boolean stopAfterExecute;
	protected UUID previousActivity;
	protected String[] nextActivity;
	protected boolean isHumanTask;
	// Internal message to describe the exit reason, e.g. exception, event called ...
	protected String exitMessage;
	
	protected Map<String,Object> parameters;

	protected String assignedUser;
	
	
}
