package de.mhus.cherry.reactive.model.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public interface IEngine {

	List<INode> searchNodes(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException;
	
	List<ICase> searchCases(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException;

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
	
}
