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
package de.mhus.app.reactive.vaadin.core;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.mhus.app.reactive.model.engine.EngineMessage;
import de.mhus.app.reactive.model.ui.INode;
import de.mhus.app.reactive.vaadin.widgets.NodeItem;
import de.mhus.app.reactive.vaadin.widgets.VCaseDetails;
import de.mhus.app.reactive.vaadin.widgets.VUserForm;
import de.mhus.app.reactive.vaadin.widgets.WidgetActivityAdapter;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.vaadin.TextInputDialog;

public class WidgetActivity extends WidgetActivityAdapter {

    private BpmSpace space;

    public WidgetActivity(BpmSpace space) {
        this.space = space;
    }

    @Override
    public void showForm(UUID id) {
        try {
            showUserForm(id);
        } catch (Exception e) {
            space.log().e("show form failed", id, e);
        }
    }

    @Override
    public void showCaseRuntime(UUID id) {
        try {
            List<EngineMessage[]> runtime = space.getEngine().getCaseRuntimeMessages(id.toString());
            space.showRuntime(runtime);
        } catch (Exception e) {
            space.log().e("show case runtime failed", id, e);
        }
    }

    @Override
    public void doCaseArchive(UUID id) {
        try {
            space.getEngine().doArchive(id);
        } catch (Exception e) {
            space.log().e("do case archive failed", id, e);
        }
    }

    @Override
    public void showNodeDetails(UUID id) {
        try {
            INode item = space.getEngine().getNode(id.toString());
            space.showNodeDetails(new NodeItem(space.getEngine(), item));
        } catch (Exception e) {
            space.log().e("show node details failed", id, e);
        }
    }

    @Override
    public void showCaseDetails(UUID itemId) {
        VCaseDetails panel =
                new VCaseDetails() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onCancel() {
                        space.log().i("show case details failed", itemId, "Cancel");
                        space.showNodeList();
                    }
                };

        panel.configure(space.getEngine(), itemId.toString());

        space.setContent(panel);
    }

    @Override
    public void showNodeRuntime(UUID id) {
        try {
            EngineMessage[] runtime = space.getEngine().getNodeRuntimeMessage(id.toString());
            LinkedList<EngineMessage[]> list = new LinkedList<>();
            list.add(runtime);
            space.showRuntime(list);
        } catch (Exception e) {
            space.log().e("show runtime {1} failed", id, e);
        }
    }

    public void showUserForm(UUID itemId) throws Exception {

        INode node = space.getEngine().getNode(itemId.toString());

        VUserForm form =
                new VUserForm(space.getEngine(), node) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected DefRoot checkForm(DefRoot form) {
                        try { // test serialization
                            String serialized = MCast.serializeToString(form);
                            form = (DefRoot) MCast.unserializeFromString(serialized, null);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        return form;
                    }

                    @Override
                    protected void onFormCancel() {
                        space.log().i("form cancel failed", node, "Cancel");
                        space.showNodeList();
                    }

                    @Override
                    protected void onFormSubmit(INode node, MProperties properties) {
                        space.log().i("Submit", node);
                        try {
                            space.getEngine().submitUserTask(node.getId().toString(), properties);
                            space.showNodeList();
                        } catch (Exception e) {
                            space.log().e("submit {1} failed", node, e);
                        }
                    }

                    @Override
                    protected MProperties onAction(
                            INode node, String action, MProperties properties) {
                        space.log().i("Action", node, action);
                        try {
                            MProperties res =
                                    space.getEngine()
                                            .onUserTaskAction(
                                                    node.getId().toString(), action, properties);
                            return res;
                        } catch (Exception e) {
                            space.log().e("action failed", node, action, e);
                        }
                        return null;
                    }
                };

        space.setContent(form);
    }

    @Override
    public void doDue(final UUID id) {

        TextInputDialog.show(
                space.getUI(),
                "Set due days",
                "",
                "Insert number of days or leave empty",
                "Set",
                "Cancel",
                new TextInputDialog.Listener() {

                    @Override
                    public boolean validate(String txtInput) {
                        if (MString.isEmpty(txtInput)) {
                            try {
                                space.getEngine().setDueDays(id.toString(), -1);
                                space.doRefresh();
                                return true;
                            } catch (Exception e) {
                                space.log().e("set due days failed", id, e);
                                return false;
                            }
                        }
                        int val = M.to(txtInput, -1);
                        if (val < 0 || val > 1000) return false;
                        try {
                            space.getEngine().setDueDays(id.toString(), val);
                            space.doRefresh();
                        } catch (Exception e) {
                            space.log().e("set due days failed", id, val, e);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void onClose(TextInputDialog dialog) {}
                });
    }
}
