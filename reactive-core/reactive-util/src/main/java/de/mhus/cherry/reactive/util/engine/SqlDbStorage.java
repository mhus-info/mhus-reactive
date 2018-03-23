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
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
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
			prop.put("name", caze.getCanonicalName());
			prop.put("closedCode", caze.getClosedCode());
			prop.put("closedMessage", caze.getClosedMessage() == null ? "" : caze.getClosedMessage() );
			prop.put("uri", caze.getUri());
			if (exists) {
				DbStatement sta = con.createStatement("UPDATE " + prefix + "_case_ SET "
						+ "content_=$content$,"
						+ "modified_=$modified$,"
						+ "state_=$state$,"
						+ "closed_code_=$closedCode$,"
						+ "closed_message_=$closedMessage$"
						+ " WHERE id_=$id$");
				sta.executeUpdate(prop);
				sta.close();
			} else {
				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_case_ ("
						+ "id_,"
						+ "content_,"
						+ "created_,"
						+ "modified_,"
						+ "state_,"
						+ "uri_,"
						+ "closed_code_,"
						+ "closed_message_,"
						+ "name_"
						+ ") VALUES ("
						+ "$id$,"
						+ "$content$,"
						+ "$created$,"
						+ "$modified$,"
						+ "$state$,"
						+ "$uri$,"
						+ "$closedCode$,"
						+ "$closedMessage$,"
						+ "$name$"
						+ ")");
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
				DbStatement sta = con.createStatement("DELETE FROM " + prefix + "_node_ WHERE case_=$id$");
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
			prop.put("type", flow.getType());
			prop.put("name", flow.getCanonicalName());
			prop.put("signal", flow.getSignalsAsString());
			prop.put("message", flow.getMessagesAsString());
			if (flow.getAssignedUser() != null)
				prop.put("assigned", flow.getAssignedUser());
			Entry<String, Long> scheduled = flow.getNextScheduled();
			long scheduledLong = 0;
			if (scheduled != null && scheduled.getValue() != null)
				scheduledLong = scheduled.getValue();
			prop.put("scheduled", scheduledLong);
			
			if (exists) {
				DbStatement sta = con.createStatement("UPDATE " + prefix + "_node_ SET "
						+ "content_=$content$,"
						+ "modified_=$modified$,"
						+ "state_=$state$,"
						+ "type_=$type$,"
						+ "signal_=$signal$,"
						+ "message_=$message$,"
						+ "scheduled_=$scheduled$,"
						+ "assigned_=$assigned$"
						+ " WHERE id_=$id$");
				sta.executeUpdate(prop);
				sta.close();
			} else {
				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_node_ ("
						+ "id_,"
						+ "case_,"
						+ "content_,"
						+ "created_,"
						+ "modified_,"
						+ "state_,"
						+ "type_,"
						+ "name_,"
						+ "scheduled_,"
						+ "assigned_,"
						+ "signal_,"
						+ "message_"
						+ ") VALUES ("
						+ "$id$,"
						+ "$case$,"
						+ "$content$,"
						+ "$created$,"
						+ "$modified$,"
						+ "$state$,"
						+ "$type$,"
						+ "$name$,"
						+ "$scheduled$,"
						+ "$assigned$,"
						+ "$signal$,"
						+ "$message$"
						+ ")");
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
				sta = con.createStatement("SELECT id_,uri_,name_,state_ FROM " + prefix + "_case_");
			} else {
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,uri_,name_,state_ FROM " + prefix + "_case_ WHERE state_=$state$");
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
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_");
			} else 
			if (caseId == null) {
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE state_=$state$");
			} else
			if (state == null) {
				prop.setString("case", caseId.toString());
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE case_=$case$");
			} else {
				prop.setString("case", caseId.toString());
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE state_=$state$ and case_=$case$");
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
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE scheduled_ <= $scheduled$");
			} else {
				prop.setLong("scheduled", scheduled);
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE state_=$state$ and scheduled_ <= $scheduled$");
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
	public Result<PNodeInfo> getSignalFlowNodes(STATE_NODE state, String signal) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			if (state == null) {
				prop.setString("signal", "%" + PNode.getSignalAsString(signal) + "%");
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE signal_ like $signal$");
			} else {
				prop.setString("signal", "%" + PNode.getSignalAsString(signal) + "%");
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE state_=$state$ and signal_ like $signal$");
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
			if (state == null && caseId == null) {
				prop.setString("message", "%" + PNode.getMessageAsString(message) + "%");
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE message_ like $message$");
			} else
			if (caseId == null) {
				prop.setString("message", "%"+ PNode.getMessageAsString(message) +"%");
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE state_=$state$ and message_ like $message$");
			} else
			if (state == null) {
				prop.setString("message", "%"+ PNode.getMessageAsString(message) +"%");
				prop.put("case", caseId);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE case_=$case$ and message_ like $message$");
			} else {
				prop.setString("message", "%"+ PNode.getMessageAsString(message) +"%");
				prop.put("case", caseId);
				prop.put("state", state);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE case_=$case$ and state_=$state$ and message_ like $message$");
			}
				
			DbResult res = sta.executeQuery(prop);
			return new SqlResultNode(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public Result<PNodeInfo> getAssignedFlowNodes(String user) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			if (user == null) {
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE assigned is null");
			} else {
				prop.setString("user", user);
				sta = con.createStatement("SELECT id_,case_,name_,assigned_,state_,type_ FROM " + prefix + "_node_ WHERE assigned_=$user$");
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
				PCaseInfo out = new PCaseInfo(UUID.fromString(
						res.getString("id_")), 
						res.getString("uri_"), 
						res.getString("name_"),
						toCaseState(res.getInt("state_"))
						);
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
				PNodeInfo out = new PNodeInfo(
						UUID.fromString(res.getString("id_")), 
						UUID.fromString(res.getString("case_")),
						res.getString("name_"),
						res.getString("assigned_"),
						toNodeState(res.getInt("state_")),
						toNodeType(res.getInt("type_"))
						);
				hasNext = res.next();
				return out;
			} catch (Exception e) {
				throw new MRuntimeException(e);
			}
		}
	}

	protected static STATE_CASE toCaseState(int index) {
		if (index < 0 || index >= STATE_CASE.values().length) return STATE_CASE.CLOSED;
		return STATE_CASE.values()[index];
	}

	protected static STATE_NODE toNodeState(int index) {
		if (index < 0 || index >= STATE_NODE.values().length) return STATE_NODE.CLOSED;
		return STATE_NODE.values()[index];
	}
	
	protected static TYPE_NODE toNodeType(int index) {
		if (index < 0 || index >= TYPE_NODE.values().length) return TYPE_NODE.NODE;
		return TYPE_NODE.values()[index];
	}
	
	public void dumpNodes() {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = con.createStatement("SELECT * FROM " + prefix + "_node_");
			DbResult res = sta.executeQuery(prop);
			while(res.next()) {
				System.out.println("NODE:");
				for (String name : res.getColumnNames())
					if (!name.toLowerCase().equals("content_"))
					System.out.println("  " + name + ": " + res.getString(name));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
