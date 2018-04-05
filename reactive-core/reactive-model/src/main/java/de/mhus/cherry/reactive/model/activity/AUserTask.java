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
package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.IndexValuesProvider;
import de.mhus.cherry.reactive.model.util.UserForm;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.errors.MException;

public interface AUserTask<P extends APool<?>> extends ATask<P>, IndexValuesProvider {

	UserForm createForm();
	
	IProperties getFormValues() throws MException;
	
	void doSubmit(IProperties values) throws MException;

	
}
