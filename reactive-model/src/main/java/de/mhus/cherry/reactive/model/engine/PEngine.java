package de.mhus.cherry.reactive.model.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public class PEngine implements Externalizable {

	protected Map<String,Object> parameters;

	public PEngine() {
		
	}
	public PEngine(PEngine clone) {
		parameters = new HashMap<>(clone.getParameters());
	}

	public Map<String, Object> getParameters() {
		if (parameters == null) parameters = new HashMap<>();
		return parameters;
	}

	@Override
	public String toString() {
		return "Engine: " + parameters;
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(1);
		out.writeObject(parameters);
		out.flush();
	}
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version != 1) throw new IOException("Wrong version: " + version);
		parameters = (Map<String, Object>) in.readObject();
	}

}
