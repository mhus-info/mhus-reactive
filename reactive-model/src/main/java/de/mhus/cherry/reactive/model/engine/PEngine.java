package de.mhus.cherry.reactive.model.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MString;

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
	
	/**
	 * Enabled means the version can be instantiated.
	 * @param name
	 * @return true if it's allowed
	 */
	public boolean isProcessEnabled(String name) {
		String v = MString.afterIndex(name, ':');
		String n = MString.beforeIndex(name, ':');
		String[] versions = String.valueOf(getParameters().getOrDefault("process:" + n + ":versions","")).split(",");
		return MCollection.contains(versions, v);
	}
	
	public boolean isProcessEnabled(String name, String version) {
		String[] versions = String.valueOf(getParameters().getOrDefault("process:" + name + ":versions","")).split(",");
		return MCollection.contains(versions, version);
	}
	
	public void enableProcessVersion(String deployedName) {
		// add version
		String v = MString.afterIndex(deployedName, ':');
		String n = MString.beforeIndex(deployedName, ':');
		String[] versions = String.valueOf(getParameters().getOrDefault("process:" + n + ":versions","")).split(",");
		if (!MCollection.contains(versions, v)) {
			versions = MCollection.append(versions, v);
			getParameters().put("process:" + n + ":versions", MString.join(versions,','));
		}
	}
	
	/**
	 * Active means this is the default version for a new process instance
	 * @param deployedName
	 */
	public void activateProcessVersion(String deployedName) {
		String v = MString.afterIndex(deployedName, ':');
		String n = MString.beforeIndex(deployedName, ':');
		getParameters().put("process:" + n + ":active", v);
	}
	public String getActiveProcessVersion(String processName) {
		if (processName.indexOf(':') >= 0) processName = MString.beforeIndex(processName, ':');
		return String.valueOf(getParameters().get("process:" + processName + ":active"));
	}
	public boolean isProcessActive(String deployedName) {
		String v = MString.afterIndex(deployedName, ':');
		String n = MString.beforeIndex(deployedName, ':');
		return v.equals(String.valueOf(getParameters().get("process:" + n + ":active")));
	}

}
