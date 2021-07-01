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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.VerticalLayout;

import de.mhus.app.reactive.model.ui.IEngine;
import de.mhus.app.reactive.model.ui.INode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;
import de.mhus.lib.form.MForm;
import de.mhus.lib.form.MutableMForm;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.vaadin.form.VaadinForm;

@SuppressWarnings("deprecation")
public class VUserForm extends VerticalLayout implements ActionHandler {

    private static final long serialVersionUID = 1L;
    private static final Log log = Log.getLog(VUserForm.class);
    private INode node;
    private Button bCancel;
    private PropertiesDataSource dataSource;
    private VaadinForm vForm;
    private IEngine engine;

    public VUserForm(IEngine engine, INode node) {
        this.engine = engine;
        this.node = node;
        vForm = createForm();

        addComponent(vForm);
        setExpandRatio(vForm, 1);
        // add buttons
        HorizontalLayout toolBar = new HorizontalLayout();
        addComponent(toolBar);

        bCancel = new Button("Exit");
        toolBar.addComponent(bCancel);
        bCancel.addClickListener(
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        onFormCancel();
                    }
                });
        setSizeFull();

        //		Button bSubmit = new Button("Submit");
        //		toolBar.addComponent(bSubmit);
        //		bSubmit.addClickListener(new Button.ClickListener() {
        //			private static final long serialVersionUID = 1L;
        //			@Override
        //			public void buttonClick(ClickEvent event) {
        //				onFormSubmit(node, dataSource.getProperties());
        //			}
        //
        //		});

    }

    protected void onFormSubmit(INode node2, MProperties properties) {}

    protected MProperties onAction(INode node2, String action, MProperties properties) {
        return null;
    }

    protected void onFormCancel() {
        try {
            engine.doUnassignUserTask(node.getId().toString());
        } catch (Exception e) {
            log.w(e);
        }
    }

    protected VaadinForm createForm() {
        try {
            IFormInformation hForm = engine.getNodeUserForm(node.getId().toString());
            DefRoot form = hForm.getForm();
            form = checkForm(form);
            dataSource = new PropertiesDataSource();
            dataSource.setProperties(
                    new MProperties(engine.getNodeUserFormValues(node.getId().toString())));

            VaadinForm vform = new VaadinForm();
            //			vform.setShowInformation(true);
            MutableMForm mform = new MutableMForm(form);
            mform.setDataSource(dataSource);
            mform.setActionHandler(this);
            Class<? extends FormControl> control = hForm.getFormControl();
            if (control != null) {
                FormControl controlObject = hForm.createFormControl();
                mform.setControl(controlObject);
            }
            vform.setForm(mform);
            vform.doBuild();
            vform.setSizeFull();

            mform.setBuilder(vform.getBuilder());
            if (mform.getControl() != null) mform.getControl().setup();

            return vform;
        } catch (Throwable t) {
            log.e(node, t);
        }
        return null;
    }

    protected DefRoot checkForm(DefRoot form) {
        return form;
    }

    @Override
    public void doAction(MForm form, String action) {
        if (action.startsWith("submit:")) {
            action = action.substring(7);
            MProperties p = dataSource.getProperties();
            p.putAll(IProperties.explodeToMProperties(action));
            onFormSubmit(node, p);
        } else if (action.startsWith("action:")) {
            action = action.substring(7);
            MProperties p = dataSource.getProperties();
            p = onAction(node, action, p);
            if (p != null) {
                dataSource.getProperties().putAll(p);
                vForm.getBuilder().doUpdateValues();
            }
        } else if (action.startsWith("control:")) {
            action = action.substring(8);
            form.getControl().doAction(action);
        } else log.w("Unknown action type " + action);
    }
}
