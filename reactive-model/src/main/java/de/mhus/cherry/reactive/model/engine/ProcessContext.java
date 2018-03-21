package de.mhus.cherry.reactive.model.engine;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.lib.errors.MException;

public interface ProcessContext<P extends APool<?>> {

	P getPool();
	ASwimlane<P> getSwimlane();
	PNode createActivity(Class<? extends AActivity<?>> next) throws Exception;
	PCase getPCase();
	EPool getEPool();
	PNode getPNode();
	EElement getENode();
	AElement<?> getANode();
	String getUri();
	EProcess getEProcess();
	PNode getPRuntime();
	RuntimeNode getARuntime();
	void saveRuntime() throws IOException;
	AaaProvider getAaaProvider();
	void doCloseActivity(RuntimeNode runtimeNode, UUID caseId) throws MException, IOException;
	EEngine getEEngine();
	
}
