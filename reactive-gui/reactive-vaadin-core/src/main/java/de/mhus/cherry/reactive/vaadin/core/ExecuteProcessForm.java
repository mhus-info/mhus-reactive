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
package de.mhus.cherry.reactive.vaadin.core;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.VerticalLayout;

@SuppressWarnings("deprecation")
public class ExecuteProcessForm extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private BpmSpace space;
    private TextArea cmd;

    public ExecuteProcessForm(BpmSpace bpmSpace, String uri) {
        space = bpmSpace;

        setSizeFull();

        cmd = new TextArea();
        cmd.setSizeFull();
        if (uri != null) cmd.setValue(uri);
        addComponent(cmd);
        setExpandRatio(cmd, 1);

        HorizontalLayout toolBar = new HorizontalLayout();
        addComponent(toolBar);

        Button bCancel = new Button("Cancel");
        toolBar.addComponent(bCancel);
        bCancel.addClickListener(
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        space.showNodeList();
                    }
                });

        Button bSubmit = new Button("Submit");
        toolBar.addComponent(bSubmit);
        bSubmit.addClickListener(
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        try {
                            String u = cmd.getValue();
                            u = u.replace("\n", "");
                            u = u.replace("\r", "");
                            Object ret = space.getEngine().doExecute(u);
                            Notification.show(
                                    "Started: " + ret, Notification.TYPE_TRAY_NOTIFICATION);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Notification.show(e.toString(), Notification.TYPE_ERROR_MESSAGE);
                        }
                        space.showNodeList();
                    }
                });
    }
}
