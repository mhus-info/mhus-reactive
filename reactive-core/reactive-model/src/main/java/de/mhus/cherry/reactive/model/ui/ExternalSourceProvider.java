package de.mhus.cherry.reactive.model.ui;

import java.util.List;

import de.mhus.cherry.reactive.model.engine.PNodeInfo;

public interface ExternalSourceProvider {

	List<String> getPropertyNames(String scope, PNodeInfo nodeInfo);
	
	String getPropertyValue(String scope, PNodeInfo nodeInfo, String name);
	
}
