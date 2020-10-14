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
package de.mhus.app.reactive.engine.util;

import java.io.IOException;
import java.util.UUID;

import de.mhus.app.reactive.engine.EngineContext;
import de.mhus.app.reactive.model.activity.AActivity;
import de.mhus.app.reactive.model.activity.APool;
import de.mhus.app.reactive.model.engine.CaseLock;
import de.mhus.app.reactive.model.engine.EElement;
import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.RuntimeNode;
import de.mhus.app.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public interface PCaseLock extends CaseLock {

    void closeRuntime(UUID nodeId) throws MException, IOException;

    void closeFlowNode(EngineContext context, PNode pNode, STATE_NODE state)
            throws IOException, NotFoundException;

    void saveRuntime(PNode pRuntime, RuntimeNode aRuntime) throws IOException;

    void savePCase(EngineContext context) throws IOException, NotFoundException;

    void savePCase(APool<?> aPool, boolean init) throws IOException, NotFoundException;

    void doNodeErrorHandling(EngineContext context, PNode pNode, Throwable t);

    PNode createActivity(EngineContext context, PNode previous, EElement start) throws Exception;

    void doNodeLifecycle(EngineContext context, PNode flow) throws Exception;

    UUID createStartPoint(EngineContext context, EElement start) throws Exception;

    void saveFlowNode(EngineContext context, PNode flow, AActivity<?> activity)
            throws IOException, NotFoundException;

    void doFlowNode(PNode pNode);

    void setPCase(PCase pCase) throws MException;

    void resetPCase();

    UUID getCaseId();

    long getOwnerThreadId();
}
