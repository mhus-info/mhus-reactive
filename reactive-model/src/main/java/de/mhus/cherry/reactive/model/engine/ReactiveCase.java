package de.mhus.cherry.reactive.model.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.UUID;

public class ReactiveCase implements Externalizable {
	
	public enum STATE {NEW, READY, SCHEDULED, FAILED, SUSPENDED, CLOSED};
	
	protected UUID id;
	protected String customId;
	protected String uri;
	protected String name;
	protected long creationDate;
	protected String createdBy;
	protected STATE state = STATE.NEW;
	protected long scheduled;
	protected Map<String,Object> parameters;

	public ReactiveCase() {}

	public ReactiveCase(UUID id, String customId, String uri, String name, long creationDate, String createdBy, STATE state,
	        long scheduled, Map<String, Object> parameters) {
		this.id = id;
		this.customId = customId;
		this.uri = uri;
		this.name = name;
		this.creationDate = creationDate;
		this.createdBy = createdBy;
		this.state = state;
		this.scheduled = scheduled;
		this.parameters = parameters;
	}

	public UUID getId() {
		return id;
	}
	
	public String getCustomId() {
		return customId;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public STATE getState() {
		return state;
	}

	public long getScheduled() {
		return scheduled;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(1);
		out.writeObject(id);
		out.writeObject(customId);
		out.writeObject(uri);
		out.writeObject(name);
		out.writeLong(creationDate);
		out.writeObject(createdBy);
		out.writeObject(state);
		out.writeLong(scheduled);
		out.writeObject(parameters);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		
		int version = in.readInt();
		if (version != 1) throw new IOException("Wrong version: " + version);
		
		id = (UUID) in.readObject();
		customId = (String) in.readObject();
		uri = (String) in.readObject();
		name = (String) in.readObject();
		creationDate = in.readLong();
		createdBy = (String) in.readObject();
		state = (STATE) in.readObject();
		scheduled = in.readLong();
		parameters = (Map<String, Object>) in.readObject();
	}
	
}
