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

public class WidgetActivityAdapter implements WidgetActivityDelegate {

    @Override
    public void showNodeDetails(UUID id) {}

    @Override
    public void showForm(UUID id) {}

    @Override
    public void showCaseDetails(UUID id) {}

    @Override
    public void showCaseRuntime(UUID id) {}

    @Override
    public void showNodeRuntime(UUID id) {}

    @Override
    public void doCaseArchive(UUID id) {}

    @Override
    public boolean isShowNodeAssign(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeDetails(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeRuntime(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeRefresh(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeDue(UUID id) {
        return true;
    }

    @Override
    public void doDue(UUID id) {}

    @Override
    public boolean isShowCaseDetails(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseActions(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseRefresh(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseRuntime(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseArchive(UUID id) {
        return true;
    }
}
