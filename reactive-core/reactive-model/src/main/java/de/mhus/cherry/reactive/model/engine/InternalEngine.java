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
package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.TimeoutException;

/**
 * Enhanced, not default engine features.
 *
 * @author mikehummel
 */
public interface InternalEngine {

    RuntimeNode doExecuteStartPoint(ProcessContext<?> context, EElement eMyStartPoint)
            throws Exception;

    Object execute(MUri uri, IProperties parameters) throws Exception;

    void doNodeErrorHandling(PNode closeNode, String error) throws Exception;

    CaseLock getCaseLockByNode(UUID nodeId, String operation, Object... tagPairs) throws MException;

    CaseLock getCaseLock(PNodeInfo nodeInfo, String operation, Object... tagPairs)
            throws TimeoutException;

    CaseLock getCaseLock(PCaseInfo caseInfo, String operation, Object... tagPairs)
            throws TimeoutException;

    CaseLock getCaseLock(PNode node, String operation, Object... tagPairs) throws TimeoutException;

    CaseLock getCaseLockOrNull(PNodeInfo nodeInfo, String operation, Object... tagPairs);

    CaseLock getCaseLockOrNull(UUID caseId, String operation, Object... tagPairs)
            throws TimeoutException;

    CaseLock getCaseLock(UUID caseId, String operation, Object... tagPairs) throws TimeoutException;
}
