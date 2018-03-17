package de.mhus.cherry.reactive.util.engine;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbPool;

public class SqlDbStorage implements StorageProvider {

	private DbPool pool;

	public SqlDbStorage(DbPool pool) {
		this.pool = pool;
		init();
	}
	
	public void init() {
		
	}

	@Override
	public void saveCase(PCase caze) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			
		} catch (Exception e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public PCase loadCase(UUID id) throws IOException, NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCaseAndFlowNodes(UUID id) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveFlowNode(PNode flow) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PNode loadFlowNode(UUID id) throws IOException, NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<UUID> getCases(STATE_CASE state) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<UUID> getFlowNodes(UUID caseId, STATE_NODE state) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<UUID> getScheduledFlowNodes(STATE_NODE state, long scheduled) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<UUID> getSignaledFlowNodes(STATE_NODE state, String signal) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<UUID> getMessageFlowNodes(UUID caseId, STATE_NODE state, String message) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PEngine loadEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveEngine(PEngine engine) {
		// TODO Auto-generated method stub
		
	}

}
