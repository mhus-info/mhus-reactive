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
package de.mhus.cherry.reactive.engine.ui;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.mhus.cherry.reactive.model.ui.INodeDescription;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class UiNodeDescription extends MLog implements INodeDescription, Externalizable {

	private String uri;
	private String name;
	private IProcess process;

	public UiNodeDescription(UiEngine ui, String uri, String name) {
		this.uri = uri;
		this.name = name;
		try {
			process = ui.getProcess(uri);
		} catch (MException e) {
			log().d(uri,e);
		}
	}

	@Override
	public String getDisplayName() {
		return process.getDisplayName(uri, name);
	}

	@Override
	public String getDescription() {
		return process.getDescription(uri, name);
	}

	@Override
	public String getPropertyName(String property) {
		return process.getPropertyName(uri, name, property);
	}

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(1);
        out.writeObject(uri);
        out.writeObject(name);
        // out.writeObject(process);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if ( in.readInt() != 1) throw new IOException("Wrong object version");
        uri = (String) in.readObject();
        name = (String) in.readObject();
        // process = (IProcess) in.readObject();
    }

}
