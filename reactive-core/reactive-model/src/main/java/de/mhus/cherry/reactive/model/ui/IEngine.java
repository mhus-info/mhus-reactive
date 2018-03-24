package de.mhus.cherry.reactive.model.ui;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.lib.errors.NotFoundException;

public interface IEngine {

	List<INode> getNodes(int page, int size, UUID caseId, STATE_NODE state) throws NotFoundException, IOException;

	List<INode> searchNodes(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException;
	
	
}