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

import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;

public interface EProcess {

	/**
	 * Return the simple name of the process.
	 * 
	 * @return Simple name
	 */
	String getName();

	String getVersion();

	List<Class<? extends AElement<?>>> getElements();

	EPool getPool(String name);

	EElement getElement(String name);

	Set<String> getPoolNames();

	Set<String> getElementNames();
	
	ProcessDescription getProcessDescription();

	/**
	 * Return processName : processVersion
	 * @return The unique name processName : processVersion
	 */
	String getProcessName();

	Class<? extends AProcess> getProcessClass();

	/**
	 * Return the canonical name of the process class only.
	 * 
	 * @return canonical class name
	 */
	String getCanonicalName();

}
