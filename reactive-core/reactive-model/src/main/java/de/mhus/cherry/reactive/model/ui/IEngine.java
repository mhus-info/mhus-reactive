package de.mhus.cherry.reactive.model.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public interface IEngine {

	List<INode> searchNodes(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException;
	
	List<ICase> searchCases(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException;

	IProcess getProcess(String uri) throws MException;

	ICase getCase(String id) throws Exception;

	INode getNode(String id) throws Exception;

	Locale getLocale();

	String getUser();

	Object execute(String uri) throws Exception;
	
}
