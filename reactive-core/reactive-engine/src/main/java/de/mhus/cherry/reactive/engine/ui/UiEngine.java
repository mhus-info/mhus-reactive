package de.mhus.cherry.reactive.engine.ui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.util.EngineUtil;
import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.ICaseDescription;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.ui.INodeDescription;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.core.util.MutableUri;
import de.mhus.lib.core.util.SoftHashMap;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class UiEngine extends MLog implements IEngine {

	private Engine engine;
	private String user;
	private SoftHashMap<String, Boolean> cacheAccessRead = new SoftHashMap<>();
	private SoftHashMap<String, Boolean> cacheAccessWrite = new SoftHashMap<>();
	private SoftHashMap<UUID, Boolean> cacheAccessExecute = new SoftHashMap<>();
//	private SoftHashMap<String, EngineContext> cacheContext = new SoftHashMap<>();
	private Locale locale;
	private MProperties defaultProcessProperties = new MProperties();

	public UiEngine(Engine engine, String user, Locale locale) {
		this.engine = engine;
		this.user = user;
		this.locale = locale;
	}
	
	@Override
	public List<INode> searchNodes(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException {
		LinkedList<INode> out = new LinkedList<>();
		int cnt = 0;
		int first = page * size;
		for (PNodeInfo info : engine.storageSearchFlowNodes(criterias)) {
			if (user.equals(info.getAssigned()) || hasReadAccess(info.getUri())) {
				try {
					if (cnt >= first) {
						out.add(new UiNode(info, null));
					}
					cnt++;
				} catch (Exception e) {
					log().d(info,e);
				}
				if (out.size() >= size) break;
			}
		}
		return out;
	}

	@Override
	public List<ICase> searchCases(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException {
		LinkedList<ICase> out = new LinkedList<>();
		int cnt = 0;
		int first = page * size;
		for (PCaseInfo info : engine.storageSearchCases(criterias)) {
			if (hasReadAccess(info.getUri())) {
				try {
					if (cnt >= first) {
						out.add(new UiCase(info, null));
					}
					cnt++;
				} catch (Exception e) {
					log().d(info,e);
				}
				if (out.size() >= size) break;
			}
		}
		return out;
	}

//	private EngineContext getContext(String uri) {
//		synchronized (cacheContext) {
//			EngineContext context = cacheContext.get(uri);
//			if (context != null) return context;
//		}
//		MUri muri = MUri.toUri(uri);
//		try {
//			EProcess process = engine.getProcess(muri);
//			EPool pool = engine.getPool(process, muri);
//			EngineContext context = new EngineContext(engine);
//			context.setEProcess(process);
//			context.setEPool(pool);
//			synchronized (cacheContext) {
//				cacheContext.put(uri, context);
//			}
//			return context;
//		} catch (Throwable t) {
//			log().e(uri,user,t);
//			return null;
//		}
//	}

	public boolean hasReadAccess(String uri) {	
		synchronized (cacheAccessRead) {
			Boolean hasAccess = cacheAccessRead.get(uri);
			if (hasAccess != null) return hasAccess;
		}

		boolean hasAccess = engine.hasReadAccess(uri, user);
		synchronized (cacheAccessRead) {
			cacheAccessRead.put(uri,hasAccess);
		}
		return hasAccess;
	}
		
	public boolean hasWriteAccess(String uri) {	
		synchronized (cacheAccessWrite) {
			Boolean hasAccess = cacheAccessWrite.get(uri);
			if (hasAccess != null) return hasAccess;
		}

		boolean hasAccess = engine.hasWriteAccess(uri, user);
		synchronized (cacheAccessWrite) {
			cacheAccessWrite.put(uri,hasAccess);
		}
		return hasAccess;
	}

	public boolean hasWriteAccess(UUID nodeId) {	
		synchronized (cacheAccessExecute) {
			Boolean hasAccess = cacheAccessExecute.get(nodeId);
			if (hasAccess != null) return hasAccess;
		}

		boolean hasAccess = engine.hasExecuteAccess(nodeId, user);
		synchronized (cacheAccessExecute) {
			cacheAccessExecute.put(nodeId,hasAccess);
		}
		return hasAccess;
	}

	@Override
	public IProcess getProcess(String uri) throws MException {
		UiProcess out = new UiProcess(this, engine.getProcess(MUri.toUri(uri)));
		out.getProperties().putAll(defaultProcessProperties);
		return out;
	}

	@Override
	public ICase getCase(String id, String[] propertyNames) throws Exception {
		PCaseInfo info = EngineUtil.getCaseInfo(engine, id);
		if (!engine.hasReadAccess(info.getUri(), user))
			return null;
		
		// load properties
		Map<String,String> properties = new TreeMap<>();
		PCase caze = null;
		if (propertyNames != null && propertyNames.length == 1 && "*".equals(propertyNames[0])) {
			for (int i = 0; i < EngineConst.MAX_INDEX_VALUES; i++)
				if (info.getIndexValue(i) != null)
					properties.put(EngineConst.UI_PNODE_PREFIX + "index" + i, info.getIndexValue(i));
			caze = engine.getCase(info.getId());
			for (Entry<String, Object> entry : caze.getParameters().entrySet())
				properties.put(EngineConst.UI_CASE_PREFIX + entry.getKey(), String.valueOf(entry.getValue()));
		} else
		if (propertyNames != null) {
			for (String name : propertyNames) {
				if (name == null) continue;
				if (name.startsWith(EngineConst.UI_PNODE_PREFIX)) {
					// use switch instead of for loop to improve performance
					switch (name) {
					case "pnode.index0":
						properties.put("pnode.index0", info.getIndexValue(0));
						break;
					case "pnode.index1":
						properties.put("pnode.index1", info.getIndexValue(1));
						break;
					case "pnode.index2":
						properties.put("pnode.index2", info.getIndexValue(2));
						break;
					case "pnode.index3":
						properties.put("pnode.index3", info.getIndexValue(3));
						break;
					case "pnode.index4":
						properties.put("pnode.index4", info.getIndexValue(4));
						break;
					case "pnode.index5":
						properties.put("pnode.index5", info.getIndexValue(5));
						break;
					case "pnode.index6":
						properties.put("pnode.index6", info.getIndexValue(6));
						break;
					case "pnode.index7":
						properties.put("pnode.index7", info.getIndexValue(7));
						break;
					case "pnode.index8":
						properties.put("pnode.index8", info.getIndexValue(8));
						break;
					case "pnode.index9":
						properties.put("pnode.index9", info.getIndexValue(9));
						break;
					}
				} else
				if (name.startsWith(EngineConst.UI_CASE_PREFIX)) {
					if (caze == null) 
						caze = engine.getCase(info.getId());
					Object v = caze.getParameters().get(name.substring(EngineConst.UI_CASE_PREFIX.length()));
					if (v != null)
						properties.put(name, String.valueOf(v));
				}
			}
		}
		return new UiCase(info, properties);
	}
	
	@Override
	public INode getNode(String id, String[] propertyNames) throws Exception {
		PNodeInfo info = EngineUtil.getFlowNodeInfo(engine, id);
		if (!engine.hasReadAccess(info.getUri(), user))
			return null;
		
		// load properties
		Map<String,String> properties = new TreeMap<>();
		PCase caze = null;
		PNode node = null;
		if (propertyNames != null && propertyNames.length == 1 && "*".equals(propertyNames[0])) {
			for (int i = 0; i < EngineConst.MAX_INDEX_VALUES; i++)
				if (info.getIndexValue(i) != null)
					properties.put(EngineConst.UI_PNODE_PREFIX + "index" + i, info.getIndexValue(i));
			caze = engine.getCase(info.getCaseId());
			for (Entry<String, Object> entry : caze.getParameters().entrySet())
				properties.put(EngineConst.UI_CASE_PREFIX + entry.getKey(), String.valueOf(entry.getValue()));
			node = engine.getFlowNode(info.getId());
			for (Entry<String, Object> entry : node.getParameters().entrySet())
				properties.put(EngineConst.UI_NODE_PREFIX + entry.getKey(), String.valueOf(entry.getValue()));
		} else
		if (propertyNames != null) {
			for (String name : propertyNames) {
				if (name == null) continue;
				if (name.startsWith(EngineConst.UI_PNODE_PREFIX)) {
					// use switch instead of for loop to improve performance
					switch (name) {
					case "pnode.index0":
						properties.put("pnode.index0", info.getIndexValue(0));
						break;
					case "pnode.index1":
						properties.put("pnode.index1", info.getIndexValue(1));
						break;
					case "pnode.index2":
						properties.put("pnode.index2", info.getIndexValue(2));
						break;
					case "pnode.index3":
						properties.put("pnode.index3", info.getIndexValue(3));
						break;
					case "pnode.index4":
						properties.put("pnode.index4", info.getIndexValue(4));
						break;
					case "pnode.index5":
						properties.put("pnode.index5", info.getIndexValue(5));
						break;
					case "pnode.index6":
						properties.put("pnode.index6", info.getIndexValue(6));
						break;
					case "pnode.index7":
						properties.put("pnode.index7", info.getIndexValue(7));
						break;
					case "pnode.index8":
						properties.put("pnode.index8", info.getIndexValue(8));
						break;
					case "pnode.index9":
						properties.put("pnode.index9", info.getIndexValue(9));
						break;
					}
				} else
				if (name.startsWith(EngineConst.UI_CASE_PREFIX)) {
					if (caze == null) 
						caze = engine.getCase(info.getCaseId());
					Object v = caze.getParameters().get(name.substring(EngineConst.UI_CASE_PREFIX.length()));
					if (v != null)
						properties.put(name, String.valueOf(v));
				} else
				if (name.startsWith(EngineConst.UI_NODE_PREFIX)) {
					if (node == null)
						node = engine.getFlowNode(info.getId());
					Object v = node.getParameters().get(name.substring(EngineConst.UI_NODE_PREFIX.length()));
					if (v != null)
						properties.put(name, String.valueOf(v));
				}
			}
		}
		
		return new UiNode(info, properties);
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	public MProperties getDefaultProcessProperties() {
		return defaultProcessProperties;
	}

	@Override
	public String getUser() {
		return user;
	}

	public void setDefaultProcessProperties(MProperties defaultProcessProperties) {
		this.defaultProcessProperties = defaultProcessProperties;
	}

	@Override
	public Object execute(String uri) throws Exception {
		MutableUri u = (MutableUri) MUri.toUri(uri);
		u.setUsername(user);
		return engine.execute(u);
	}

	@Override
	public ICaseDescription getCaseDescription(String uri) throws Exception {
		return new UiCaseDescription(this,uri);
	}

	@Override
	public INodeDescription getNodeDescritpion(String uri, String name) throws Exception {
		return new UiNodeDescription(this, uri, name);
	}
	
}
