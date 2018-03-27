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

import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.XmlConfigFile;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MRuntimeException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbPool;
import de.mhus.lib.sql.DbResult;
import de.mhus.lib.sql.DbStatement;

public class SqlDbStorage extends MLog implements StorageProvider {

	private static final int MAX_INDEX_VALUES = Math.min( 10, EngineConst.MAX_INDEX_VALUES);
	private static final String INDEX_COLUMNS = ",index0_,index1_,index2_,index3_,index4_,index5_,index6_,index7_,index8_,index9_";
	private static final String CASE_COLUMNS = "id_,uri_,name_,state_,custom_,customer_,process_,version_,pool_,created_,modified_,priority_,score_" + INDEX_COLUMNS;
	private static final String NODE_COLUMNS = "id_,case_,name_,assigned_,state_,type_,uri_,custom_,customer_,process_,version_,pool_,created_,modified_,priority_,score_" + INDEX_COLUMNS;
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
			prop.put("content", 		inStream);
			prop.put("modified", 		new Date());
			prop.put("state", 			caze.getState());
			prop.put("closedCode", 		caze.getClosedCode());
			prop.put("closedMessage", 	M.trunc(caze.getClosedMessage() == null ? "" : caze.getClosedMessage(), 400) );
			
			if (!exists) {
				prop.put("created", 		new Date());
				prop.put("custom", 			M.trunc(caze.getCustomId(), 700));
				prop.put("customer", 			M.trunc(caze.getCustomerId(), 700));
				prop.put("name", 			caze.getCanonicalName());
				prop.put("uri", 			M.trunc(caze.getUri(), 700));
				MUri u = MUri.toUri(caze.getUri());
				prop.put("process", MString.beforeIndex(u.getLocation(), ':'));
				prop.put("version", MString.afterIndex(u.getLocation(),':'));
				prop.put("pool", u.getPath());
			}
			
			if (exists) {
				String sql = "UPDATE " + prefix + "_case_ SET "
						+ "content_=$content$,"
						+ "modified_=$modified$,"
						+ "state_=$state$,"
						+ "closed_code_=$closedCode$,"
						+ "closed_message_=$closedMessage$";

				if (caze.getIndexValues() != null) {
					String[] idx = caze.getIndexValues();
					for (int i = 0; i < MAX_INDEX_VALUES; i++)
						if (idx.length > i && idx[i] != null) {
							prop.put("index" + i, M.trunc(idx[i], 300));
							sql = sql + ",index" + i + "_=$index"+i+"$";
						}
				}
				
				sql = sql + " WHERE id_=$id$";
				
				DbStatement sta = con.createStatement(sql);
				sta.executeUpdate(prop);
				sta.close();
			} else {
				
				if (caze.getIndexValues() != null) {
					String[] idx = caze.getIndexValues();
					for (int i = 0; i < MAX_INDEX_VALUES; i++)
						if (idx.length > i) 
							prop.put("index" + i, M.trunc(idx[i], 300));
				}

				DbStatement sta = con.createStatement("INSERT INTO " + prefix + "_case_ ("
						+ "id_,"
						+ "content_,"
						+ "created_,"
						+ "modified_,"
						+ "state_,"
						+ "uri_,"
						+ "closed_code_,"
						+ "closed_message_,"
						+ "name_,"
						+ "custom_,"
						+ "customer_,"
						+ "process_,"
						+ "version_,"
						+ "pool_,"
						+ "priority_,"
						+ "score_,"
						+ "index0_,"
						+ "index1_,"
						+ "index2_,"
						+ "index3_,"
						+ "index4_,"
						+ "index5_,"
						+ "index6_,"
						+ "index7_,"
						+ "index8_,"
						+ "index9_"
						+ ") VALUES ("
						+ "$id$,"
						+ "$content$,"
						+ "$created$,"
						+ "$modified$,"
						+ "$state$,"
						+ "$uri$,"
						+ "$closedCode$,"
						+ "$closedMessage$,"
						+ "$name$,"
						+ "$custom$,"
						+ "$customer$,"
						+ "$process$,"
						+ "$version$,"
						+ "$pool$,"
						+ "100,"
						+ "0,"
						+ "$index0$,"
						+ "$index1$,"
						+ "$index2$,"
						+ "$index3$,"
						+ "$index4$,"
						+ "$index5$,"
						+ "$index6$,"
						+ "$index7$,"
						+ "$index8$,"
						+ "$index9$"
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
			prop.put("content", 	inStream);
			prop.put("modified", 	new Date());
			prop.put("state", 		flow.getState());
			prop.put("type", 		flow.getType());
			prop.put("signal", 		M.trunc(flow.getSignalsAsString(), 700));
			prop.put("message", 	M.trunc(flow.getMessagesAsString(), 700));
			
			if (!exists) {
				PCaseInfo caze = loadCaseInfo(flow.getCaseId());
				if (caze == null) throw new IOException("Case "+ flow.getCaseId()+" not found to create node " + flow.getId());
				prop.put("name", 		M.trunc(flow.getCanonicalName(), 700));
				prop.put("case",		flow.getCaseId());
				prop.put("created", 	new Date());
				prop.put("custom", 		M.trunc(caze.getCustomId(), 700));
				prop.put("customer", 		M.trunc(caze.getCustomerId(), 700));
				prop.put("uri", 		M.trunc(caze.getUri(), 700));
				MUri u = MUri.toUri(caze.getUri());
				prop.put("process", MString.beforeIndex(u.getLocation(), ':'));
				prop.put("version", MString.afterIndex(u.getLocation(),':'));
				prop.put("pool", u.getPath());
			}
			
			if (flow.getAssignedUser() != null)
				prop.put("assigned", flow.getAssignedUser());
			Entry<String, Long> scheduled = flow.getNextScheduled();
			long scheduledLong = 0;
			if (scheduled != null && scheduled.getValue() != null)
				scheduledLong = scheduled.getValue();
			prop.put("scheduled", scheduledLong);
			
			if (exists) {
				String sql = "UPDATE " + prefix + "_node_ SET "
						+ "content_=$content$,"
						+ "modified_=$modified$,"
						+ "state_=$state$,"
						+ "type_=$type$,"
						+ "signal_=$signal$,"
						+ "message_=$message$,"
						+ "scheduled_=$scheduled$,"
						+ "assigned_=$assigned$";
				
				if (flow.getIndexValues() != null) {
					String[] idx = flow.getIndexValues();
					for (int i = 0; i < MAX_INDEX_VALUES; i++)
						if (idx.length > i && idx[i] != null) {
							prop.put("index" + i, M.trunc(idx[i], 300));
							sql = sql + ",index" + i + "_=$index"+i+"$";
						}
				}
				
				sql = sql + " WHERE id_=$id$";
						
				DbStatement sta = con.createStatement(sql);
				sta.executeUpdate(prop);
				sta.close();
			} else {
				
				if (flow.getIndexValues() != null) {
					String[] idx = flow.getIndexValues();
					for (int i = 0; i < MAX_INDEX_VALUES; i++)
						if (idx.length > i) 
							prop.put("index" + i, M.trunc(idx[i], 300));
				}

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
						+ "message_,"
						+ "uri_,"
						+ "custom_,"
						+ "customer_,"
						+ "process_,"
						+ "version_,"
						+ "pool_,"
						+ "priority_,"
						+ "score_,"
						+ "index0_,"
						+ "index1_,"
						+ "index2_,"
						+ "index3_,"
						+ "index4_,"
						+ "index5_,"
						+ "index6_,"
						+ "index7_,"
						+ "index8_,"
						+ "index9_"
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
						+ "$message$,"
						+ "$uri$,"
						+ "$custom$,"
						+ "$customer$,"
						+ "$process$,"
						+ "$version$,"
						+ "$pool$,"
						+ "100,"
						+ "0,"
						+ "$index0$,"
						+ "$index1$,"
						+ "$index2$,"
						+ "$index3$,"
						+ "$index4$,"
						+ "$index5$,"
						+ "$index6$,"
						+ "$index7$,"
						+ "$index8$,"
						+ "$index9$"
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
				sta = con.createStatement("SELECT "+CASE_COLUMNS+" FROM " + prefix + "_case_");
			} else {
				prop.put("state", state);
				sta = con.createStatement("SELECT "+CASE_COLUMNS+" FROM " + prefix + "_case_ WHERE state_=$state$");
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
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_");
			} else 
			if (caseId == null) {
				prop.put("state", state);
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE state_=$state$");
			} else
			if (state == null) {
				prop.setString("case", caseId.toString());
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE case_=$case$");
			} else {
				prop.setString("case", caseId.toString());
				prop.put("state", state);
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE state_=$state$ and case_=$case$");
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
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE scheduled_ <= $scheduled$");
			} else {
				prop.setLong("scheduled", scheduled);
				prop.put("state", state);
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE state_=$state$ and scheduled_ <= $scheduled$");
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
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE signal_ like $signal$");
			} else {
				prop.setString("signal", "%" + PNode.getSignalAsString(signal) + "%");
				prop.put("state", state);
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE state_=$state$ and signal_ like $signal$");
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
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE message_ like $message$");
			} else
			if (caseId == null) {
				prop.setString("message", "%"+ PNode.getMessageAsString(message) +"%");
				prop.put("state", state);
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE state_=$state$ and message_ like $message$");
			} else
			if (state == null) {
				prop.setString("message", "%"+ PNode.getMessageAsString(message) +"%");
				prop.put("case", caseId);
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE case_=$case$ and message_ like $message$");
			} else {
				prop.setString("message", "%"+ PNode.getMessageAsString(message) +"%");
				prop.put("case", caseId);
				prop.put("state", state);
				sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE case_=$case$ and state_=$state$ and message_ like $message$");
			}
				
			DbResult res = sta.executeQuery(prop);
			return new SqlResultNode(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public Result<PNodeInfo> searchFlowNodes(SearchCriterias search) throws IOException {
		try {
			StringBuilder sql = new StringBuilder("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ ");
			boolean whereAdded = false;
			MProperties prop = new MProperties();
			if (search.unassigned) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				sql.append("assigned is null ");
			} else
			if (search.assigned != null)
			{
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				prop.setString("user", search.assigned);
				sql.append("assigned=$user$ ");
			}
			
			if (search.nodeState != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				prop.put("state", search.nodeState);
				sql.append("state_=$state$ ");
			}

			if (search.type != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				prop.put("type", search.type);
				sql.append("type_=$type$ ");
			}
			
			if (search.uri != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("uri", search.uri, prop, sql);
			}

			if (search.name != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("name", search.name, prop, sql);
			}

			if (search.custom != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("custom", search.custom,prop,sql);
			}
			
			if (search.customer != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("customer", search.customer,prop,sql);
			}
			
			if (search.process != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("process", search.process,prop,sql);
			}
			
			if (search.version != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("version", search.version,prop,sql);
			}
			
			if (search.pool != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("pool", search.pool,prop,sql);
			}

			if (search.caseId != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				prop.put("case", search.caseId);
				sql.append("case_=$case$ ");
			}

			if (search.index != null) {
				for (int i = 0; i < MAX_INDEX_VALUES; i++) {
					if (search.index.length > i && search.index[i] != null) {
						if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
						whereAdded = true;
						prop.setString("index" + i, search.index[i]);
						addFilter("index" + i, search.index[i], prop, sql);
					}
				}
			}
			
			// at last order
			if (search.order != null) {
				sql.append("ORDER BY ").append(search.order.name().toLowerCase()).append("_ ");
				if (!search.orderAscending)
					sql.append("DESC ");
			}
			
			DbConnection con = pool.getConnection();
			DbStatement sta = con.createStatement(sql.toString());
			DbResult res = sta.executeQuery(prop);
			return new SqlResultNode(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}	

	@Override
	public Result<PCaseInfo> searchCases(SearchCriterias search) throws IOException {
		try {
			StringBuilder sql = new StringBuilder("SELECT "+CASE_COLUMNS+" FROM " + prefix + "_case_ ");
			boolean whereAdded = false;
			MProperties prop = new MProperties();
			
			if (search.caseState != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				prop.put("state", search.nodeState);
				sql.append("state_=$state$ ");
			}
			
			if (search.uri != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("uri", search.uri,prop,sql);
			}
			
			if (search.name != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("name", search.name,prop,sql);
			}
			
			if (search.custom != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("custom", search.custom,prop,sql);
			}
			
			if (search.customer != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("customer", search.customer,prop,sql);
			}
			
			if (search.process != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("process", search.process,prop,sql);
			}
			
			if (search.version != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("version", search.version,prop,sql);
			}
			
			if (search.pool != null) {
				if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
				whereAdded = true;
				addFilter("pool", search.pool,prop,sql);
			}

			if (search.index != null) {
				for (int i = 0; i < MAX_INDEX_VALUES; i++) {
					if (search.index.length > i && search.index[i] != null) {
						if (whereAdded) sql.append("AND "); else sql.append("WHERE ");
						whereAdded = true;
						addFilter("index"+i, search.index[i],prop,sql);
					}
				}
			}
			
			// at last order
			if (search.order != null) {
				sql.append("ORDER BY ").append(search.order.name().toLowerCase()).append("_ ");
				if (!search.orderAscending)
					sql.append("DESC ");
			}

			DbConnection con = pool.getConnection();
			DbStatement sta = con.createStatement(sql.toString());
			DbResult res = sta.executeQuery(prop);
			return new SqlResultCase(con,res);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}	

	private void addFilter(String name, String value, MProperties prop, StringBuilder sql) {
		prop.put(name, value);
		
		if (value.startsWith("%") || value.endsWith("%"))
			sql.append(name + "_ like $"+name+"$ ");
		else
			sql.append(name + "_=$"+name+"$ ");
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
				PCaseInfo out = newPCase(res);
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
				PNodeInfo out = newPNode(res);
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

	protected static PNodeInfo newPNode(DbResult res) throws Exception {
		PNodeInfo out = new PNodeInfo(
				UUID.fromString(res.getString("id_")), 
				UUID.fromString(res.getString("case_")),
				res.getString("name_"),
				res.getString("assigned_"),
				toNodeState(res.getInt("state_")),
				toNodeType(res.getInt("type_")),
				res.getString("uri_"),
				res.getString("custom_"),
				res.getString("customer_"),
				res.getLong("created_"),
				res.getLong("modified_"),
				res.getInt("priority_"),
				res.getInt("score_"),
				new String[] {
						res.getString("index0_"),
						res.getString("index1_"),
						res.getString("index2_"),
						res.getString("index3_"),
						res.getString("index4_"),
						res.getString("index5_"),
						res.getString("index6_"),
						res.getString("index7_"),
						res.getString("index8_"),
						res.getString("index9_")
					}
				);
		return out;
	}

	protected static PCaseInfo newPCase(DbResult res) throws Exception {
		PCaseInfo out = new PCaseInfo(UUID.fromString(
				res.getString("id_")), 
				res.getString("uri_"), 
				res.getString("name_"),
				toCaseState(res.getInt("state_")),
				res.getString("custom_"),
				res.getString("customer_"),
				res.getLong("created_"),
				res.getLong("modified_"),
				res.getInt("priority_"),
				res.getInt("score_"),
				new String[] {
						res.getString("index0_"),
						res.getString("index1_"),
						res.getString("index2_"),
						res.getString("index3_"),
						res.getString("index4_"),
						res.getString("index5_"),
						res.getString("index6_"),
						res.getString("index7_"),
						res.getString("index8_"),
						res.getString("index9_")
					}
				);
		return out;
	}

	protected static STATE_NODE toNodeState(int index) {
		if (index < 0 || index >= STATE_NODE.values().length) return STATE_NODE.CLOSED;
		return STATE_NODE.values()[index];
	}
	
	protected static TYPE_NODE toNodeType(int index) {
		if (index < 0 || index >= TYPE_NODE.values().length) return TYPE_NODE.NODE;
		return TYPE_NODE.values()[index];
	}
	
	public void dumpCases() {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = con.createStatement("SELECT * FROM " + prefix + "_case_");
			DbResult res = sta.executeQuery(prop);
			while(res.next()) {
				System.out.println("CASE:");
				for (String name : res.getColumnNames())
					if (!name.toLowerCase().equals("content_"))
					System.out.println("  " + name + ": " + res.getString(name));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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


	@Override
	public PCaseInfo loadCaseInfo(UUID caseId) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			prop.put("id", caseId);
			sta = con.createStatement("SELECT "+CASE_COLUMNS+" FROM " + prefix + "_case_ WHERE id_=$id$");
			DbResult res = sta.executeQuery(prop);
			if (!res.next()) {
				res.close();
				con.close();
				return null;
			}
			PCaseInfo out = newPCase(res);
			res.close();
			con.close();
			return out;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public PNodeInfo loadFlowNodeInfo(UUID nodeId) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			prop.put("id", nodeId);
			sta = con.createStatement("SELECT "+NODE_COLUMNS+" FROM " + prefix + "_node_ WHERE id_=$id$");
			DbResult res = sta.executeQuery(prop);
			if (!res.next()) {
				res.close();
				con.close();
				return null;
			}
			PNodeInfo out = newPNode(res);
			res.close();
			con.close();
			return out;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean setNodePriority(UUID nodeId, int priority) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			prop.put("id", nodeId);
			prop.put("value", priority);
			sta = con.createStatement("UPDATE " + prefix + "_node_ SET priority_=$value$ WHERE id_=$id$");
			int res = sta.executeUpdate(prop);
			con.close();
			return res == 1;
		} catch (Exception e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public boolean setNodeScope(UUID nodeId, int scope) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			prop.put("id", nodeId);
			prop.put("value", scope);
			sta = con.createStatement("UPDATE " + prefix + "_node_ SET scope_=$value$ WHERE id_=$id$");
			int res = sta.executeUpdate(prop);
			con.close();
			return res == 1;
		} catch (Exception e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public boolean setCasePriority(UUID caseId, int priority) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			prop.put("id", caseId);
			prop.put("value", priority);
			sta = con.createStatement("UPDATE " + prefix + "_case_ SET priority_=$value$ WHERE id_=$id$");
			int res = sta.executeUpdate(prop);
			con.close();
			return res == 1;
		} catch (Exception e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public boolean setCaseScope(UUID caseId, int scope) throws IOException {
		try {
			DbConnection con = pool.getConnection();
			MProperties prop = new MProperties();
			DbStatement sta = null;
			prop.put("id", caseId);
			prop.put("value", scope);
			sta = con.createStatement("UPDATE " + prefix + "_case_ SET scope_=$value$ WHERE id_=$id$");
			int res = sta.executeUpdate(prop);
			con.close();
			return res == 1;
		} catch (Exception e) {
			throw new IOException(e);
		}
		
	}

}
