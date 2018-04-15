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
package de.mhus.cherry.reactive.model.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public interface IEngine {

	List<INode> searchNodes(SearchCriterias criterias, int page, int size, String[] propertyNames) throws NotFoundException, IOException;
	
	List<ICase> searchCases(SearchCriterias criterias, int page, int size, String[] propertyNames) throws NotFoundException, IOException;

	IProcess getProcess(String uri) throws MException;

	ICase getCase(String id, String[] propertyNames) throws Exception;

	INode getNode(String id, String[] propertyNames) throws Exception;

	default ICaseDescription getCaseDescription(ICase caze) throws Exception {
		return getCaseDescription(caze.getUri());
	}

	default INodeDescription getNodeDescription(INode node) throws Exception {
		return getNodeDescritpion(node.getUri(), node.getCanonicalName());
	}

	ICaseDescription getCaseDescription(String uri) throws Exception;

	INodeDescription getNodeDescritpion(String uri, String name) throws Exception;

	Locale getLocale();

	String getUser();

	Object execute(String uri) throws Exception;

	void doArchive(UUID caseId) throws Exception;
	
}
