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
import de.mhus.lib.core.IProperties;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public interface IEngine {

	List<INode> searchNodes(SearchCriterias criterias, int page, int size, String ... propertyNames) throws NotFoundException, IOException;
	
	List<ICase> searchCases(SearchCriterias criterias, int page, int size, String ... propertyNames) throws NotFoundException, IOException;

	IProcess getProcess(String uri) throws MException;

	IPool getPool(String uri) throws MException;
	
	ICase getCase(String id, String ... propertyNames) throws Exception;

	INode getNode(String id, String ... propertyNames) throws Exception;

	default ICaseDescription getCaseDescription(ICase caze) throws Exception {
		return getCaseDescription(caze.getUri());
	}

	default INodeDescription getNodeDescription(INode node) throws Exception {
		return getNodeDescription(node.getUri(), node.getCanonicalName());
	}

	ICaseDescription getCaseDescription(String uri) throws Exception;

	INodeDescription getNodeDescription(String uri, String name) throws Exception;

	Locale getLocale();

	String getUser();

	Object doExecute(String uri) throws Exception;

	Object doExecute(String uri, IProperties properties) throws Exception;
	
	void doArchive(UUID caseId) throws Exception;
	
	/**
	 * Return the surrounding model for the node.
	 * 
	 * @param nodeId
	 * @return The model
	 * @throws Exception 
	 */
	IModel getModel(UUID nodeId) throws Exception;

	/**
	 * Return all node models for the running case.
	 * 
	 * @param caseId
	 * @return all node models
	 * @throws Exception 
	 */
	IModel[] getCaseModels(UUID caseId) throws Exception;

	/**
	 * Will close this UI engine instance. Not the central engine. For some
	 * implementations this will be helpful to release resources.
	 * 
	 */
	void close();

	boolean isClosed();
}
