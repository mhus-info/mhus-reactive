/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MDate;

public class EngineMessage {

	public enum TYPE {OTHER,FLOW,ERROR,CONNECT,START}
	public static final String FLOW_PREFIX = "flow:";
	public static final String CONNECT_PREFIX = "connect:";
	public static final String START_PREFIX = "start:";
	public static final String ERROR_PREFIX = "error:";
	private TYPE type = TYPE.OTHER;
	private String msg;
	private UUID fromNode;
	private UUID toNode;
	private String originalMsg;
	private long ts;

	public EngineMessage(String msg) {
		originalMsg = msg;
		int p = msg.indexOf('|');
		if (p >= 0) {
			ts = MCast.tolong(msg.substring(0, p), 0);
			msg = msg.substring(p+1);
		}
		p = msg.indexOf(':');
		if (p >= 0) {
			String t = msg.substring(0, p+1);
			switch (t) {
			case FLOW_PREFIX: type = TYPE.FLOW;break;
			case ERROR_PREFIX: type = TYPE.ERROR;break;
			case CONNECT_PREFIX: type = TYPE.CONNECT;break;
			case START_PREFIX: type = TYPE.START;break;
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
			case START:
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
	
	public long getTimestamp() {
		return ts;
	}
	
	@Override
	public String toString() {
		return originalMsg;
	}
	
}
