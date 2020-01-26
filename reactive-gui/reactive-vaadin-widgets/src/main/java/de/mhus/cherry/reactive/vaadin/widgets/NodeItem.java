/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.annotations.vaadin.Column;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;

public class NodeItem {

    private INode node;

    @SuppressWarnings("unused")
    private IEngine engine;

    private MUri uri;
    private IProcess process;

    public NodeItem(IEngine engine, INode node) {
        this.engine = engine;
        this.node = node;
        uri = MUri.toUri(node.getUri());
        try {
            process = engine.getProcess(node.getUri());
        } catch (MException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Column(order = 1, title = "Process Name", editable = false)
    public String getProcessName() {
        return process.getDisplayName(node.getUri(), null);
    }

    @Column(order = 2, title = "Name", editable = false)
    public String getDisplayName() {
        return process.getDisplayName(node.getUri(), node.getCanonicalName());
    }

    @Column(order = 3, title = "CutsomId", editable = false)
    public String getCustom() {
        return node.getCustomId();
    }

    @Column(order = 4, title = "Customer", editable = false)
    public String getCustomer() {
        return node.getCustomerId();
    }

    @Column(order = 5, title = "id", editable = false, elapsed = false)
    public UUID getId() {
        return node.getId();
    }

    @Column(order = 6, title = "CName", editable = false, elapsed = false)
    public String getName() {
        return node.getCanonicalName();
    }

    @Column(order = 7, title = "State", editable = false)
    public STATE_NODE getState() {
        return node.getNodeState();
    }

    @Column(order = 8, title = "Type", editable = false)
    public TYPE_NODE getType() {
        return node.getType();
    }

    @Column(order = 9, title = "Index 1", editable = false)
    public String getIndex0() {
        return node.getProperties().get("pnode.index0");
    }

    @Column(order = 10, title = "Index 2", editable = false)
    public String getIndex1() {
        return node.getProperties().get("pnode.index1");
    }

    @Column(order = 11, title = "Index 3", editable = false)
    public String getIndex2() {
        return node.getProperties().get("pnode.index2");
    }

    @Column(order = 12, title = "Index 4", editable = false)
    public String getIndex3() {
        return node.getProperties().get("pnode.index3");
    }

    @Column(order = 13, title = "Index 5", editable = false)
    public String getIndex4() {
        return node.getProperties().get("pnode.index4");
    }

    @Column(order = 14, title = "Index 6", editable = false, elapsed = false)
    public String getIndex5() {
        return node.getProperties().get("pnode.index5");
    }

    @Column(order = 15, title = "Index 7", editable = false, elapsed = false)
    public String getIndex6() {
        return node.getProperties().get("pnode.index6");
    }

    @Column(order = 16, title = "Index 8", editable = false, elapsed = false)
    public String getIndex7() {
        return node.getProperties().get("pnode.index7");
    }

    @Column(order = 17, title = "Index 9", editable = false, elapsed = false)
    public String getIndex8() {
        return node.getProperties().get("pnode.index8");
    }

    @Column(order = 18, title = "Index 10", editable = false, elapsed = false)
    public String getIndex9() {
        return node.getProperties().get("pnode.index9");
    }

    @Column(order = 19, title = "URI", editable = false, elapsed = false)
    public String getUri() {
        return node.getUri();
    }

    @Column(order = 20, title = "Assigned User", editable = false)
    public String getAssigned() {
        return node.getAssigned();
    }

    @Column(order = 21, title = "Case Id", editable = false, elapsed = false)
    public UUID getCaseId() {
        return node.getCaseId();
    }

    @Column(order = 22, title = "Process", editable = false, elapsed = false)
    public String getProcess() {
        return uri.getLocation();
    }

    @Column(order = 23, title = "Pool", editable = false, elapsed = false)
    public String getPool() {
        return uri.getPath();
    }

    @Column(order = 24, title = "Actor", editable = false, elapsed = false)
    public String getActor() {
        return node.getActor();
    }

    @Override
    public boolean equals(Object in) {
        if (in == null || !(in instanceof NodeItem)) return false;
        return node.getId().equals(((NodeItem) in).getId());
    }
}
