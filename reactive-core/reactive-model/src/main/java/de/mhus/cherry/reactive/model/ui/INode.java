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
package de.mhus.cherry.reactive.model.ui;

import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;

public interface INode {

    String getUri();

    String getCanonicalName();

    STATE_NODE getNodeState();

    UUID getId();

    String getCustomId();

    String getCustomerId();

    TYPE_NODE getType();

    UUID getCaseId();

    Map<String, String> getProperties();

    long getCreated();

    long getModified();

    int getPriority();

    int getScore();

    String getAssigned();

    String getActor();
}
