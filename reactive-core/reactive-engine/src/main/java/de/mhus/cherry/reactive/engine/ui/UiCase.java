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

import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.lib.annotations.generic.Public;
import de.mhus.lib.core.MLog;

public class UiCase extends MLog implements ICase {

	private PCaseInfo info;
	private Map<String, String> properties;
	
	public UiCase(PCaseInfo info, Map<String, String> properties) {
		this.info = info;
		this.properties = properties;
	}

	@Override
	@Public
	public String getUri() {
		return info.getUri();
	}

	@Override
	@Public
	public String getCanonicalName() {
		return info.getCanonicalName();
	}
	
	@Override
	@Public
	public String getCustomId() {
		return info.getCustomId();
	}
	
	@Override
	@Public
	public STATE_CASE getState() {
		return info.getState();
	}
	
	@Override
	@Public
	public UUID getId() {
		return info.getId();
	}
	
	@Override
	@Public
	public String getCustomerId() {
		return info.getCustomerId();
	}

	@Override
	@Public
	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	@Public
	public long getCreated() {
		return info.getCreated();
	}

	@Override
	@Public
	public long getModified() {
		return info.getModified();
	}

	@Override
	@Public
	public int getPriority() {
		return info.getPriority();
	}

	@Override
	@Public
	public int getScore() {
		return info.getScore();
	}

}
