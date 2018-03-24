package de.mhus.cherry.reactive.model.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MSystem;

public class RuntimeNode extends MLog implements AElement<APool<?>>, ContextRecipient {

	private static final String CLOSE_ACTIVITY = "closeActivity";
	private static final String MSG_PREFIX = "msg.";
	
	private Map<String, Object> parameters;

	private ProcessContext<?> context;

	private void addFlowMessage(UUID id, String msg) {
		addMessage(EngineMessage.FLOW_PREFIX + id + "," + msg);
	}

	private synchronized void addMessage(String msg) {
		if (parameters == null) parameters = new HashMap<>();
		int next = getNetMessageId();
		parameters.put(MSG_PREFIX + next, msg);
		save();
	}

	private int getNetMessageId() {
		int cnt = 0;
		while (parameters.containsKey(MSG_PREFIX + cnt)) cnt++;
		return cnt;
	}

	private void addFlowConnect(UUID previousId, UUID id) {
		addMessage(EngineMessage.CONNECT_PREFIX + previousId + "," + id);
	}

	
	public Map<String, Object> exportParamters() {
		return parameters;
	}
	
	public void importParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public void doEvent(String name, PNode flow, Object[] args) {
		if (name.equals("createActivity")) {
			UUID previousId = (UUID) args[2];
			addFlowConnect(previousId, flow.getId());
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < args.length; i++)
				MSystem.serialize(sb, args[i], null);
			addFlowMessage(	flow.getId(), sb.toString());
		}
	}

//	public void doActivityFailed(PNode flow) {
//		addFlowMessage(flow.getId(),flow.getName() + " doActivityFailed");
//	}

//	public void doNodeLifecycle(PNode flow, boolean init) {
//		addFlowMessage(flow.getId(),flow.getName() + " doNodeLifecycle init:" + init);
//	}

//	public void doActivityStop(PNode flow) {
//		addFlowMessage(flow.getId(), flow.getName() + " doActivityStop");
//	}

//	public void createActivity(PNode flow, UUID previousId) {
//		addFlowConnect(previousId, flow.getId());
//	}

	public void doErrorMsg(PNode flow, Object ... objects) {
		addMessage(EngineMessage.ERROR_PREFIX +  flow.getId() + "," + flow.getName() + " " + MSystem.toString("Error", objects));
		save();
	}

//	public void closedActivity(PNode flow) {
//		addFlowMessage(flow.getId(), flow.getName() + " closedActivity");
//	}

	public void setCloseActivity(UUID id) {
		parameters.put(CLOSE_ACTIVITY, id.toString());
		save();
	}
	public void save() {
		if (context != null) {
			try {
				context.saveRuntime();
			} catch (IOException e) {
				log().e(e);
			}
		} else {
			log().i("Context not set in runtime");
		}
	}

	public void close() {
		Object closeId = parameters.get(CLOSE_ACTIVITY);
		if (closeId == null) return;
		try {
			context.doCloseActivity(this, UUID.fromString(String.valueOf(closeId)));
		} catch (Throwable t) {
			log().e(closeId,t);
		}
	}

	@Override
	public void setContext(ProcessContext<?> context) {
		this.context = context;
	}

	public List<EngineMessage> getMessages() {
		LinkedList<EngineMessage> out = new LinkedList<>();
		int cnt = 0;
		while (parameters.containsKey(MSG_PREFIX + cnt)) {
			out.add(new EngineMessage(String.valueOf(parameters.get(MSG_PREFIX + cnt))));
			cnt++;
		}
		return out;
	}
	
}
