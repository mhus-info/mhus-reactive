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
package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.annotations.vaadin.Column;
import de.mhus.lib.errors.MException;

public class CaseItem {

	private ICase caze;
	private IEngine engine;

	public CaseItem(IEngine engine, ICase caze) {
		this.engine = engine;
		this.caze = caze;
	}

	@Column(order=1,title="id", editable=false,elapsed=false)
	public UUID getId() {
		return caze.getId();
	}
		
	@Column(order=2,title="CName", editable=false)
	public String getName() {
		return caze.getCanonicalName();
	}

	@Column(order=3,title="CutsomId", editable=false)
	public String getCustom() {
		return caze.getCustomId();
	}

	@Column(order=4,title="URI", editable=false, elapsed=false)
	public String getUri() {
		return caze.getUri();
	}
	
	@Column(order=5,title="Index 1", editable=false)
	public String getIndex0() {
		return caze.getProperties().get("pnode.index0");
	}

	@Column(order=6,title="Index 2", editable=false)
	public String getIndex1() {
		return caze.getProperties().get("pnode.index1");
	}

	@Column(order=7,title="Index 3", editable=false)
	public String getIndex2() {
		return caze.getProperties().get("pnode.index2");
	}

	@Column(order=8,title="Index 4", editable=false, elapsed=false)
	public String getIndex3() {
		return caze.getProperties().get("pnode.index3");
	}

	@Column(order=9,title="Index 5", editable=false, elapsed=false)
	public String getIndex4() {
		return caze.getProperties().get("pnode.index4");
	}

	@Column(order=10,title="Index 6", editable=false, elapsed=false)
	public String getIndex5() {
		return caze.getProperties().get("pnode.index5");
	}

	@Column(order=11,title="Index 7", editable=false, elapsed=false)
	public String getIndex6() {
		return caze.getProperties().get("pnode.index6");
	}

	@Column(order=12,title="Index 8", editable=false, elapsed=false)
	public String getIndex7() {
		return caze.getProperties().get("pnode.index7");
	}

	@Column(order=13,title="Index 9", editable=false, elapsed=false)
	public String getIndex8() {
		return caze.getProperties().get("pnode.index8");
	}

	@Column(order=14,title="Index 10", editable=false, elapsed=false)
	public String getIndex9() {
		return caze.getProperties().get("pnode.index9");
	}
	
	@Column(order=15,title="State", editable=false)
	public STATE_CASE getState() {
		return caze.getState();
	}
	
	@Column(order=16,title="Customer", editable=false)
	public String getCustomer() {
		return caze.getCustomerId();
	}

	@Column(order=17,title="Name", editable=false)
	public String getDisplayName() {
		try {
			return engine.getProcess(caze.getUri()).getDisplayName(caze.getUri(), null);
		} catch (MException e) {
			return "?";
		}
	}

	@Override
	public boolean equals(Object in) {
		if (in == null || !(in instanceof CaseItem)) return false;
		return caze.getId().equals( ((CaseItem)in).getId() );
	}

}
