package de.mhus.cherry.reactive.util.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.XmlConfigFile;
import de.mhus.lib.core.io.PipedStream;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbPool;
import de.mhus.lib.sql.DbResult;
import de.mhus.lib.sql.DbStatement;

public class SqlDbStorage extends MLog implements StorageProvider {

	private DbPool pool;
	private String prefix = "null";

	public SqlDbStorage(DbPool pool) {
		this.pool = pool;
		init();
	}
	
	public void init() {
		try {
			URL url = MSystem.locateResource(this, "SqlDbStorage.xml");
			DbConnection con = pool.getConnection();
			XmlConfigFile data = new XmlConfigFile(url.openStream());
			data.setString("prefix", prefix);
			pool.getDialect().createStructure(data, con, null, false);
			con.close();
		} catch (Exception e) {
			log().e(e);
		}
	}

	@Override
	public void saveCase(PCase caze) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			boolean exists = false;
			MProperties prop = new MProperties();
			prop.put("id", caze.getId());
			{
				DbStatement sta = con.createStatement("SELECT id_ FROM " + prefix + "_case_ WHERE id_=$id$");
				DbResult res = sta.executeQuery(prop);
				exists = res.next();
				res.close();
				sta.close();
			}	
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			caze.writeExternal(new ObjectOutputStream(outStream));
			ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
			prop.put("content", inStream);
			prop.put("created", new Date());
			prop.put("modified", new Date());
			prop.put("state", caze.getState());
			prop.put("uri", caze.getUri());
			if (exists) {
				DbStatement sta = con.createStatement("UPDATE " + prefix + "_case_ SET content_=$content$,modified_=$modified$,state_=$state$ WHERE id_=$id$");
				sta.executeUpdate(prop);
				sta.close();
			} else {
				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_case_ (id_,content_,created_,modified_,state_,uri_) VALUES ($id$,$content$,$created$,$modified$,$state$,$uri$)");
				sta.executeUpdate(prop);
				sta.close();
			}
			con.commit();
			con.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public PCase loadCase(UUID id) throws IOException, NotFoundException {
		PCase caze = null;
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			prop.put("id", id);
			DbStatement sta = con.createStatement("SELECT content_ FROM " + prefix + "_case_ WHERE id_=$id$");
			DbResult res = sta.executeQuery(prop);
			if (res.next()) {
				InputStream in = res.getBinaryStream("content_");
				caze = new PCase();
				caze.readExternal(new ObjectInputStream(in));
			}
			res.close();
			con.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		if (caze == null) throw new NotFoundException("case",id);
		return caze;
	}

	@Override
	public void deleteCaseAndFlowNodes(UUID id) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			prop.put("id", id);
			{
				DbStatement sta = con.createStatement("DELETE FROM " + prefix + "_case_ WHERE id_=$id$");
				sta.execute(prop);
			}
			{
				DbStatement sta = con.createStatement("DELETE FROM " + prefix + "_node_ WHERE id_=$id$");
				sta.execute(prop);
			}
			con.commit();
			con.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void saveFlowNode(PNode flow) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			boolean exists = false;
			MProperties prop = new MProperties();
			prop.put("id", flow.getId());
			{
				DbStatement sta = con.createStatement("SELECT id_ FROM " + prefix + "_node_ WHERE id_=$id$");
				DbResult res = sta.executeQuery(prop);
				exists = res.next();
				res.close();
				sta.close();
			}	
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			flow.writeExternal(new ObjectOutputStream(outStream));
			ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
			prop.put("content", inStream);
			prop.put("created", new Date());
			prop.put("modified", new Date());
			prop.put("state", flow.getState());
			prop.put("name", flow.getName());
			if (exists) {
				DbStatement sta = con.createStatement("UPDATE " + prefix + "_node_ SET content_=$content$,modified_=$modified$,state_=$state$ WHERE id_=$id$");
				sta.executeUpdate(prop);
				sta.close();
			} else {
				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_node_ (id_,content_,created_,modified_,state_,name_) VALUES ($id$,$content$,$created$,$modified$,$state$,$name$)");
				sta.executeUpdate(prop);
				sta.close();
			}
			con.commit();
			con.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public PNode loadFlowNode(UUID id) throws IOException, NotFoundException {
		PNode node = null;
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			prop.put("id", id);
			DbStatement sta = con.createStatement("SELECT content_ FROM " + prefix + "_node_ WHERE id_=$id$");
			DbResult res = sta.executeQuery(prop);
			if (res.next()) {
				InputStream in = res.getBinaryStream("content_");
				node = new PNode();
				node.readExternal(new ObjectInputStream(in));
			}
			res.close();
			con.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		if (node == null) throw new NotFoundException("node",id);
		return node;
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
	public PEngine loadEngine() throws IOException, NotFoundException {
		PEngine engine = null;
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			prop.put("id", "engine");
			DbStatement sta = con.createStatement("SELECT content_ FROM " + prefix + "_engine_ WHERE id_=$id$");
			DbResult res = sta.executeQuery(prop);
			if (res.next()) {
				InputStream in = res.getBinaryStream("content_");
				engine = new PEngine();
				engine.readExternal(new ObjectInputStream(in));
			}
			res.close();
			con.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		if (engine == null) throw new NotFoundException("engine");
		return engine;
	}

	@Override
	public void saveEngine(PEngine engine) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			boolean exists = false;
			MProperties prop = new MProperties();
			prop.put("id", "engine");
			{
				DbStatement sta = con.createStatement("SELECT id_ FROM " + prefix + "_engine_ WHERE id_=$id$");
				DbResult res = sta.executeQuery(prop);
				exists = res.next();
				res.close();
				sta.close();
			}	
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			engine.writeExternal(new ObjectOutputStream(outStream));
			ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
			prop.put("content", inStream);
			prop.put("created", new Date());
			prop.put("modified", new Date());
			if (exists) {
				DbStatement sta = con.createStatement("UPDATE " + prefix + "_engine_ SET content_=$content$,modified_=$modified$ WHERE id_=$id$");
				sta.executeUpdate(prop);
				sta.close();
				
			} else {
				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_engine_ (id_,content_,created_,modified_) VALUES ($id$,$content$,$created$,$modified$)");
				sta.executeUpdate(prop);
				sta.close();
			}
			con.commit();
			con.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
