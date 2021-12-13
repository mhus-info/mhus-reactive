/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
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

public interface WidgetActivityDelegate {

    void showNodeDetails(UUID id);

    void showForm(UUID id);

    void showCaseDetails(UUID id);

    void showCaseRuntime(UUID id);

    void showNodeRuntime(UUID id);

    void doCaseArchive(UUID id);

    boolean isShowNodeAssign(UUID id);

    boolean isShowNodeDetails(UUID id);

    boolean isShowNodeRuntime(UUID id);

    boolean isShowNodeRefresh(UUID id);

    boolean isShowNodeDue(UUID id);

    void doDue(UUID id);

    boolean isShowCaseDetails(UUID id);

    boolean isShowCaseActions(UUID id);

    boolean isShowCaseRefresh(UUID id);

    boolean isShowCaseRuntime(UUID id);

    boolean isShowCaseArchive(UUID id);

    void showCaseNodes(UUID id);

    boolean isShowCaseNodes(UUID id);
}
