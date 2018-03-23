package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

public class EngineMessage {

	public enum TYPE {OTHER,FLOW,ERROR,CONNECT}
	public static final String FLOW_PREFIX = "flow:";
	public static final String CONNECT_PREFIX = "connect:";
	public static final String ERROR_PREFIX = "error:";
	private TYPE type = TYPE.OTHER;
	private String msg;
	private UUID fromNode;
	private UUID toNode;
	private String originalMsg;

	public EngineMessage(String msg) {
		originalMsg = msg;
		int p = msg.indexOf(':');
		if (p >= 0) {
			String t = msg.substring(0, p+1);
			switch (t) {
			case FLOW_PREFIX: type = TYPE.FLOW;break;
			case ERROR_PREFIX: type = TYPE.ERROR;break;
			case CONNECT_PREFIX: type = TYPE.CONNECT;break;
			}
			if (type != TYPE.OTHER)
				msg = msg.substring(p+1);
			switch (type) {
			case CONNECT:
				p = msg.indexOf(',');
				if (p > 0) {
					fromNode = UUID.fromString(msg.substring(0, p));
					toNode = UUID.fromString(msg.substring(p+1));
				}
				break;
			case FLOW:
				p = msg.indexOf(',');
				if (p > 0) {
					fromNode = UUID.fromString(msg.substring(0, p));
					msg = msg.substring(p+1);
				}
				break;
			case ERROR:
			case OTHER:
				break;
			}
		}
		
		this.msg = msg;
	}

	public String getMessage() {
		return msg;
	}
	
	public TYPE getType() {
		return type;
	}

	public UUID getFromNode() {
		return fromNode;
	}

	public UUID getToNode() {
		return toNode;
	}
	
	@Override
	public String toString() {
		return originalMsg;
	}
	
}
