/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MProperties;

public class SearchCriterias {

	public String name;
	public String custom;
	public String customer;
	
	public String process;
	public String version;
	public String pool;

	public boolean unassigned;
	public String assigned;
	public STATE_NODE nodeState;
	public String[] index;
	public STATE_CASE caseState;
	public String uri;
	public UUID caseId;
	public TYPE_NODE type;

	public enum ORDER {CUSTOM,CUSTOMER,NAME,PROCESS,VERSION,POOL,STATE,TYPE,INDEX0,INDEX1,INDEX2,INDEX3,INDEX4,INDEX5,INDEX6,INDEX7,INDEX8,INDEX9,CREATED,MODIFIED,PRIORITY,SCORE};
	public ORDER order;
	public boolean orderAscending = true;
	
	public int priority = Integer.MAX_VALUE;
	public int score = Integer.MIN_VALUE;
	
	public SearchCriterias() {}
	
	public SearchCriterias(String[] parameters) {
		this(parameters == null? new MProperties() : MProperties.explodeToMProperties(parameters));
	}
	
	public SearchCriterias(MProperties parameters) {
		if (parameters == null) return;
		for (String k : parameters.keySet()) {
			String v = parameters.getString(k,null);
			switch (k) {
			case "state":
				try {
					caseState = STATE_CASE.valueOf(v.toUpperCase());
				} catch (Throwable t) {}
				try {
					nodeState = STATE_NODE.valueOf(v.toUpperCase());
				} catch (Throwable t) {}
				break;
			case "type":
				type = TYPE_NODE.valueOf(v.toUpperCase());
				break;
			case "priority":
				priority = M.c(v, priority);
				break;
			case "score":
				score = M.c(v, score);
				break;
			case "order":
				order = ORDER.valueOf(v.toUpperCase());
				break;
			case "ascending":
				orderAscending = M.c(v, true);
				break;
			case "uri":
				uri = v;
				break;
			case "process":
				process = v;
				break;
			case "version":
				version = v;
				break;
			case "pool":
				pool = v;
				break;
			case "custom":
				custom = v;
				break;
			case "customer":
				customer = v;
				break;
			case "name":
				name = v;
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
			default:
			}
		}
	}
	
}
