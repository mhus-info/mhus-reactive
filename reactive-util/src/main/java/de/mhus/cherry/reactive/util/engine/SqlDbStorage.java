package de.mhus.cherry.reactive.util.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.XmlConfigFile;
import de.mhus.lib.errors.MRuntimeException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbPool;
import de.mhus.lib.sql.DbResult;
import de.mhus.lib.sql.DbStatement;

public class SqlDbStorage extends MLog implements StorageProvider {

	private DbPool pool;
	private String prefix;
	
	public SqlDbStorage(DbPool pool, String prefix) {
		this.pool = pool;
		this.prefix = prefix;
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
			prop.put("closedCode", caze.getClosedCode());
			prop.put("closedMessage", caze.getClosedMessage() == null ? "" : caze.getClosedMessage() );
			prop.put("uri", caze.getUri());
			if (exists) {
				DbStatement sta = con.createStatement("UPDATE " + prefix + "_case_ SET content_=$content$,modified_=$modified$,state_=$state$,closed_code_=$closedCode$,closed_message_=$closedMessage$ WHERE id_=$id$");
				sta.executeUpdate(prop);
				sta.close();
			} else {
				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_case_ (id_,content_,created_,modified_,state_,uri_,closed_code_,closed_message_) VALUES ($id$,$content$,$created$,$modified$,$state$,$uri$,$closedCode$,$closedMessage$)");
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
			prop.put("case",flow.getCaseId());
			prop.put("created", new Date());
			prop.put("modified", new Date());
			prop.put("state", flow.getState());
			prop.put("name", flow.getName());
			prop.put("signal", flow.getSignalsAsString());
			prop.put("message", flow.getMessagesAsString());
			Entry<String, Long> scheduled = flow.getNextScheduled();
			long scheduledLong = 0;
			if (scheduled != null && scheduled.getValue() != null)
				scheduledLong = scheduled.getValue();
			prop.put("scheduled", scheduledLong);
			
			if (exists) {
				DbStatement sta = con.createStatement("UPDATE " + prefix + "_node_ SET content_=$content$,modified_=$modified$,state_=$state$,signal_=$signal$,message_=$message$,scheduled_=$scheduled$ WHERE id_=$id$");
				sta.executeUpdate(prop);
				sta.close();
			} else {
				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_node_ (id_,case_,content_,created_,modified_,state_,name_,scheduled_) VALUES ($id$,$case$,$content$,$created$,$modified$,$state$,$name$,$scheduled$)");
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
	public Result<PCaseInfo> getCases(STATE_CASE state) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			if (state == null) {
				sta = con.createStatement("SELECT id_ FROM " + prefix + "_case_");
			} else {
				prop.put("state", state);
				sta = con.createStatement("SELECT id_ FROM " + prefix + "_case_ WHERE state_=$state$");
			}
			DbResult res = sta.executeQuery(prop);
			return new SqlResultCase(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public Result<PNodeInfo> getFlowNodes(UUID caseId, STATE_NODE state) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			if (caseId == null && state == null) {
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_");
			} else 
			if (caseId == null) {
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE state_=$state$");
			} else
			if (state == null) {
				prop.setString("case", caseId.toString());
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE case_=$case$");
			} else {
				prop.setString("case", caseId.toString());
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE state_=$state$ and case_=$case$");
			}
			DbResult res = sta.executeQuery(prop);
			return new SqlResultNode(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public Result<PNodeInfo> getScheduledFlowNodes(STATE_NODE state, long scheduled) throws IOException {
		DbConnection con = null;
		try {
			con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			if (state == null) {
				prop.setLong("scheduled", scheduled);
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE scheduled_ <= $scheduled$ and scheduled_ != 0");
			} else {
				prop.setLong("scheduled", scheduled);
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE state_=$state$ and scheduled_ <= $scheduled$ and scheduled_ != 0");
			}
			DbResult res = sta.executeQuery(prop);
			return new SqlResultNode(con,res);
		} catch (Exception e) {
			try {
				if (con != null) con.close();
			} catch (Exception e2) {}
			throw new IOException(e);
		}
	}

	@Override
	public Result<PNodeInfo> getSignaledFlowNodes(STATE_NODE state, String signal) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			if (state == null) {
				prop.setString("signal", "%" + PNode.getSignalAsString(signal) + "%");
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE signal_ like $signal$");
			} else {
				prop.setString("signal", "%" + PNode.getSignalAsString(signal) + "%");
				prop.setString("state", state.name());
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE state_=$state$ and signal_ like $signal$");
			}
			DbResult res = sta.executeQuery(prop);
			return new SqlResultNode(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public Result<PNodeInfo> getMessageFlowNodes(UUID caseId, STATE_NODE state, String message) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			if (state == null) {
				prop.setString("message", "%" + PNode.getMessageAsString(message) + "%");
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE message_ like $message$");
			} else {
				prop.setString("message", "%"+ PNode.getMessageAsString(message) +"%");
				prop.setString("state", state.name());
				sta = con.createStatement("SELECT id_,case_ FROM " + prefix + "_node_ WHERE state_=$state$ and message_ like $message$");
			}
			DbResult res = sta.executeQuery(prop);
			return new SqlResultNode(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
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

	private static class SqlResultCase implements Result<PCaseInfo>, Iterator<PCaseInfo> {

		private DbResult res;
		private boolean hasNext = false;
		private DbConnection con;

		public SqlResultCase(DbConnection con, DbResult res) throws Exception {
			this.con = con;
			this.res = res;
			hasNext = res.next();
		}

		@Override
		public Iterator<PCaseInfo> iterator() {
			return this;
		}

		@Override
		public synchronized void close() {
			if (res == null) return;
			try {
				res.close();
			} catch (Exception e) {}
			con.close();
			res = null;
			con = null;
			
		}

		@Override
		public boolean hasNext() {
			if (!hasNext) {
				close();
			}
			return hasNext;
		}

		@Override
		public PCaseInfo next() {
			if (res == null) return null;
			try {
				PCaseInfo out = new PCaseInfo(UUID.fromString(res.getString("id_")));
				hasNext = res.next();
				return out;
			} catch (Exception e) {
				throw new MRuntimeException(e);
			}
		}
	}
	
	private static class SqlResultNode implements Result<PNodeInfo>, Iterator<PNodeInfo> {

		private DbResult res;
		private boolean hasNext = false;
		private DbConnection con;

		public SqlResultNode(DbConnection con, DbResult res) throws Exception {
			this.con = con;
			this.res = res;
			hasNext = res.next();
		}

		@Override
		public Iterator<PNodeInfo> iterator() {
			return this;
		}

		@Override
		public synchronized void close() {
			if (res == null) return;
			try {
				res.close();
			} catch (Exception e) {}
			con.close();
			res = null;
			con = null;
			
		}

		@Override
		public boolean hasNext() {
			if (!hasNext) {
				close();
			}
			return hasNext;
		}

		@Override
		public PNodeInfo next() {
			if (res == null) return null;
			try {
				PNodeInfo out = new PNodeInfo(UUID.fromString(res.getString("id_")), UUID.fromString(res.getString("case_")));
				hasNext = res.next();
				return out;
			} catch (Exception e) {
				throw new MRuntimeException(e);
			}
		}
	}	
}
