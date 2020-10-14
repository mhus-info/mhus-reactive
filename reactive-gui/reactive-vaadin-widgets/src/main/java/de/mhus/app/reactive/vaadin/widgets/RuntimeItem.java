/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.reactive.vaadin.widgets;

import java.util.UUID;

import de.mhus.app.reactive.model.engine.EngineMessage;
import de.mhus.app.reactive.model.engine.EngineMessage.TYPE;
import de.mhus.app.reactive.model.ui.INode;
import de.mhus.lib.annotations.vaadin.Column;
import de.mhus.lib.core.util.DateTime;

public class RuntimeItem {

    private EngineMessage item;
    private VRuntimeDetails details;

    public RuntimeItem() {}

    public RuntimeItem(VRuntimeDetails details, EngineMessage item) {
        this.details = details;
        this.item = item;
    }

    @Column(order = 1, title = "Date")
    public DateTime getDate() {
        if (item == null) return null;
        return new DateTime(item.getTimestamp());
    }

    @Column(order = 2, title = "Type")
    public TYPE getType() {
        if (item == null) return null;
        return item.getType();
    }

    @Column(order = 3, title = "From")
    public String getFromNode() {
        if (item == null) return null;
        INode node = details.getNode(item.getFromNode());
        return node == null ? "" : node.getCanonicalName();
    }

    @Column(order = 4, title = "To")
    public String getToNode() {
        if (item == null) return null;
        INode node = details.getNode(item.getToNode());
        return node == null ? "" : node.getCanonicalName();
    }

    @Column(order = 5, title = "Message")
    public String getMessage() {
        if (item == null) return null;
        return item.getMessage();
    }

    @Column(order = 6, title = "Ident", editable = false)
    public String getServerIdent() {
        if (item == null) return null;
        return item.getServerIdent();
    }

    @Column(order = 7, title = "From Id", elapsed = false)
    public UUID getFromNodeId() {
        if (item == null) return null;
        return item.getFromNode();
    }

    @Column(order = 7, title = "To Id", elapsed = false)
    public UUID getToNodeId() {
        if (item == null) return null;
        return item.getToNode();
    }
}
