/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

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

	public EngineMessage(String msg) {
		originalMsg = msg;
		int p = msg.indexOf(':');
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
	
	@Override
	public String toString() {
		return originalMsg;
	}
	
}
