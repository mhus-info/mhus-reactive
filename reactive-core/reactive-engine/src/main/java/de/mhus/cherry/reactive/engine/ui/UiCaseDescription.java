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

import de.mhus.cherry.reactive.model.ui.ICaseDescription;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class UiCaseDescription extends MLog implements ICaseDescription {

	private IProcess process;
	private String uri;

	public UiCaseDescription(UiEngine ui, String uri) {
		this.uri = uri;
		try {
			process = ui.getProcess(uri);
		} catch (MException e) {
			log().d(uri,e);
		}
	}

	@Override
	public String getDisplayName() {
		return process.getDisplayName(uri, null);
	}

	@Override
	public String getDescription() {
		return process.getDescription(uri, null);
	}

	@Override
	public String getPropertyName(String property) {
		return process.getPropertyName(uri, null, property);
	}

}
