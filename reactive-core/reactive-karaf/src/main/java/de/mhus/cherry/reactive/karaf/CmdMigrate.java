package de.mhus.cherry.reactive.karaf;

import java.util.UUID;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.pojo.DefaultFilter;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.core.pojo.PojoParser;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.core.util.Version;
import de.mhus.lib.core.util.VersionRange;

@Command(scope = "reactive", name = "pmigrate", description = "Manipulate process data in suspended cases")
@Service
public class CmdMigrate extends MLog implements Action {

	@Argument(index=0, name="uri", required=true, description="Filter for process, pool, activity in format bpm://<process>[:<version-range>][/<pool>[/<activity>]]", multiValued=false)
    String uriStr;

	@Option(name="-t", aliases="--test", description="Test it and don't do it",required=false)
	private boolean test;

	@Option(name="-i", aliases="--ids", description="Filter special case or node ids",required=false, multiValued=true)
	private String[] ids;
	
	@Option(name="-s", aliases="--suspend", description="Suspend before migration",required=false)
	private boolean suspend;

	@Option(name="-r", aliases="--resume", description="Resume after migration",required=false)
	private boolean resume;

	@Option(name="-c", aliases="--case", description="Case manipulation rule: name:<name> canonical:<name> milestone:<text> closeCode:<int> closeMessage<text> rm:<key> date:<key>=<date> string:<key>=<text> long: int: bool: uuid: double:",required=false, multiValued=true)
	private String[] caseRules;

	@Option(name="-n", aliases="--node", description="Node manipulating rule: name:<name> canonical:<name> rm:<key>  date:<key>=<date> string:<key>=<text> actor:<text> long: int: bool: uuid: double:",required=false, multiValued=true)
	private String[] nodeRules;

	private MUri uri;

	private String process;

	private VersionRange version;

	private String pool;

	private String activity;
	
	private PojoModel nodeModel = new PojoParser().parse(PNode.class,"_",null).filter(new DefaultFilter(true, false, false, false, true) ).getModel();
	private PojoModel caseModel = new PojoParser().parse(PCase.class,"_",null).filter(new DefaultFilter(true, false, false, false, true) ).getModel();

	@Override
	public Object execute() throws Exception {

		if (uriStr != null) {
			uri = MUri.toUri(uriStr);
			process = uri.getLocation();
			if (MString.isIndex(process, ':')) {
				version = new VersionRange(MString.afterIndex(process, ':'));
				process = MString.beforeIndex(process, ':');
			}
			pool = uri.getPath();
			if (MString.isIndex(pool, '/')) {
				activity = MString.afterIndex(pool, '/');
				pool = MString.beforeIndex(pool, '/');
			}
		}
		
		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		Engine engine = api.getEngine();
		
		if (suspend) {
			for (PCaseInfo info : engine.storageGetCases(null)) {
				if (info.getState() != STATE_CASE.SUSPENDED && info.getState() != STATE_CASE.CLOSED && filter(info)) {
					System.out.println("*** Suspend " + info);
					if (!test) {
						engine.suspendCase(info.getId());
						engine.prepareMigrateCase(info.getId());
					}
				}
			}
		}
		
		if (caseRules != null || nodeRules != null)
			for (PCaseInfo caseInfo : engine.storageGetCases(null)) {
				if (filter(caseInfo)) {
					
					if (caseRules != null) {
						PCase caze = engine.getCase(caseInfo.getId());
						if (caze.getState() == STATE_CASE.SUSPENDED || caze.getState() == STATE_CASE.CLOSED) {
							System.out.println(">>> Migrate " + caseInfo);
							if (!test) {
								migrateCase(caze);
								engine.savePCase(caze, null, false);
							} else {
								System.out.println(caze);
								System.out.println(caze.getParameters());
							}
						} else
						if (test)
							System.out.println("--- Incorrect state " + caseInfo);
					}
					if (nodeRules != null) {
						for (PNodeInfo nodeInfo : engine.storageGetFlowNodes(caseInfo.getId(), null)) {
							if (filter(nodeInfo)) {
								PNode node = engine.getFlowNode(nodeInfo.getId());
								if (node.getState() == STATE_NODE.SUSPENDED || node.getState() == STATE_NODE.CLOSED) {
									System.out.println(">>> Migrate " + nodeInfo);
									if (!test) {
										migrateNode(node);
										engine.saveFlowNode(node);
									} else {
										System.out.println(node);
										System.out.println(node.getParameters());
									}
								} else
									if (test)
										System.out.println("--- Incorrect state " + nodeInfo);
							}
						}
					}
				}
			}
		
		if (resume) {
			for (PCaseInfo info : engine.storageGetCases(null)) {
				if (info.getState() == STATE_CASE.SUSPENDED && filter(info)) {
					System.out.println("*** Resume " + info);
					if (!test)
						engine.resumeCase(info.getId());
				}
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private void migrateNode(PNode node) {
		
		for (String rule : nodeRules) {
			try {
				if (MString.isIndex(rule, ':')) {
					String action = MString.beforeIndex(rule, ':');
					rule = MString.afterIndex(rule, ':');
					String k = null;
					String v = null;
					if (MString.isIndex(rule, '=')) {
						k = MString.beforeIndex(rule, '=');
						v = MString.afterIndex(rule, '=');
					}
					switch (action) {
					case "name":
						nodeModel.getAttribute("name").set(node, rule);
						break;
					case "canonical":
						nodeModel.getAttribute("canonicalName").set(node, rule);
						break;
					case "type":
						nodeModel.getAttribute("type").set(node, TYPE_NODE.valueOf(rule));
						break;
					case "actor":
						nodeModel.getAttribute("actor").set(node, rule);
						break;
					case "":
					case "string":
						node.getParameters().put(k, v);
						break;
					case "date":
						node.getParameters().put(k, MCast.toDate(v, null));
						break;
					case "long":
						node.getParameters().put(k, MCast.tolong(v, 0));
						break;
					case "int":
					case "integer":
						node.getParameters().put(k, MCast.toint(v, 0));
						break;
					case "bool":
					case "boolean":
						node.getParameters().put(k, MCast.toboolean(v, false));
						break;
					case "uuid":
						node.getParameters().put(k, UUID.fromString(v));
						break;
					case "double":
						node.getParameters().put(k, MCast.todouble(v, 0));
						break;
					case "rm":
						node.getParameters().remove(rule);
						break;
					default:
						System.out.println("*** Unknown action " + action);
					}
				}
			} catch (Throwable t) {
				System.out.println("*** Rule: " + rule);
				t.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void migrateCase(PCase caze) {
		for (String rule : caseRules) {
			try {
				if (MString.isIndex(rule, ':')) {
					String action = MString.beforeIndex(rule, ':');
					rule = MString.afterIndex(rule, ':');
					String k = null;
					String v = null;
					if (MString.isIndex(rule, '=')) {
						k = MString.beforeIndex(rule, '=');
						v = MString.afterIndex(rule, '=');
					}
					switch (action) {
					case "name":
						caseModel.getAttribute("name").set(caze, rule);
						break;
					case "canonical":
						caseModel.getAttribute("canonicalName").set(caze, rule);
						break;
					case "milestone":
						caze.setMilestone(rule);
						break;
					case "closeCode":
						caseModel.getAttribute("closecode").set(caze,MCast.toint(rule, 0));
						break;
					case "closeMessage":
						caseModel.getAttribute("closemessage").set(caze,rule);
						break;
					case "":
					case "string":
						caze.getParameters().put(k, v);
						break;
					case "date":
						caze.getParameters().put(k, MCast.toDate(v, null));
						break;
					case "long":
						caze.getParameters().put(k, MCast.tolong(v, 0));
						break;
					case "int":
					case "integer":
						caze.getParameters().put(k, MCast.toint(v, 0));
						break;
					case "bool":
					case "boolean":
						caze.getParameters().put(k, MCast.toboolean(v, false));
						break;
					case "double":
						caze.getParameters().put(k, MCast.todouble(v, 0));
						break;
					case "uuid":
						caze.getParameters().put(k, UUID.fromString(v));
						break;
					case "rm":
						caze.getParameters().remove(rule);
						break;
					default:
						System.out.println("*** Unknown action " + action);
					}
				}
			} catch (Throwable t) {
				System.out.println("*** Rule: " + rule);
				t.printStackTrace();
			}
		}
	}

	private boolean filter(PNodeInfo info) {
		
		boolean filtered = false;
		if (activity != null) {
			filtered = true;
			if (!info.getCanonicalName().equals(activity))
				return false;
		}
		
		if (ids != null && nodeRules != null) {
			filtered = true;
			if (!MCollection.contains(ids, info.getId().toString())) return false;
		}

		if (!filtered)
			return false;

		return true;
	}

	private boolean filter(PCaseInfo info) {
		boolean filtered = false;
		if (uri != null) {
			filtered = true;
			MUri u = MUri.toUri(info.getUri());
			String p = u.getLocation();
			String v = MString.afterIndex(p, ':');
			p = MString.beforeIndex(p, ':');
			if (!p.equals(process)) return false;
			if (version != null && !version.includes(new Version(v))) return false;
			if (MString.isSet(pool) && !pool.equals(p)) return false;
		}
		
		if (ids != null && caseRules != null) {
			filtered = true;
			if (!MCollection.contains(ids, info.getId().toString())) return false;
		}
		
		if (!filtered)
			return false;
		
		return true;
	}

}
