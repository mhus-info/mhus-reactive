package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MString;

public class SearchCriterias {

	public SearchCriterias() {}
	
	public SearchCriterias(String[] parameters) {
		if (parameters == null) return;
		for (String param : parameters) {
			String k = MString.beforeIndex(param, '=');
			String v = MString.afterIndex(param, '=');
			switch (k) {
			case "state":
				try {
					caseState = STATE_CASE.valueOf(v.toUpperCase());
				} catch (Throwable t) {}
				try {
					nodeState = STATE_NODE.valueOf(v.toUpperCase());
				} catch (Throwable t) {}
				break;
			case "uri":
				uri = v;
				break;
			case "case":
				caseId = UUID.fromString(v);
				break;
			case "unassigned":
				unassigned = M.c(v, false);
				break;
			case "assigned":
				assigned = v;
				break;
			case "search":
				index = new String[] {v,v,v,v,v,v,v,v,v,v};
				break;
			case "index0":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index1":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index2":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index3":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index4":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index5":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index6":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index7":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index8":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			case "index9":
				if (index == null) index = new String[] {null,null,null,null,null,null,null,null,null,null};
				index[0] = v;
				break;
			}
		}
	}
	
	public boolean unassigned;
	public String assigned;
	public STATE_NODE nodeState;
	public String[] index;
	public STATE_CASE caseState;
	public String uri;
	public UUID caseId;

}
