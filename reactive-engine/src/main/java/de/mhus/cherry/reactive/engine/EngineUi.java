package de.mhus.cherry.reactive.engine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.lib.core.util.MUri;

public class EngineUi {

	private Engine engine;
	private String user;

	public EngineUi(Engine engine, String user) {
		this.engine = engine;
		this.user = user;
	}
	
	public List<PCase> getCases(int page, int size, STATE_CASE state) throws IOException {
		LinkedList<PCase> out = new LinkedList<>();
		for (PCaseInfo info : engine.storageGetCases(state)) {
			
			MUri uri = MUri.toUri(info.getUri());
			try {
				EProcess process = engine.getProcess(uri);
				EPool pool = engine.getPool(process, uri);
				pool.getPoolDescription().actorRead();
//				if (engine.getAaaProvider().)
			} catch (Throwable t) {
				t.printStackTrace(); //TODO
			}
		}
		return out;
	}
	
}
