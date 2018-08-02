/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.vaadin.widgets;

import java.io.IOException;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.util.UserForm;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.MForm;
import de.mhus.lib.form.MutableMForm;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.vaadin.form.VaadinForm;

public class VUserForm extends VerticalLayout implements ActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = Log.getLog(VUserForm.class);
	private INode node;
	private Button bCancel;
	private PropertiesDataSource dataSource;
	private VaadinForm vForm;

	public VUserForm(INode node) {
		this.node = node;
		vForm = createForm();
		
		addComponent(vForm);
		setExpandRatio(vForm, 1);
		// add buttons
		HorizontalLayout toolBar = new HorizontalLayout();
		addComponent(toolBar);
		
		bCancel = new Button("Exit");
		toolBar.addComponent(bCancel);
		bCancel.addClickListener(new Button.ClickListener() {
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

	protected void onFormSubmit(INode node2, MProperties properties) {
		
	}

	protected MProperties onAction(INode node2, MProperties properties, String action) {
		return null;
	}
	
	protected void onFormCancel() {
		try {
			node.doUnassign();
		} catch (IOException | MException e) {
			log.w(e);
		}
	}

	protected VaadinForm createForm() {
		try {
			UserForm hForm = node.getUserForm();
			DefRoot form = hForm.getRoot();
			dataSource = new PropertiesDataSource();
			dataSource.setProperties(new MProperties( node.getUserFormValues()));
			
			VaadinForm vform = new VaadinForm();
//			vform.setShowInformation(true);
			MutableMForm mform = new MutableMForm(form);
			mform.setDataSource(dataSource);
			mform.setActionHandler(this);
			Class<? extends FormControl> control = node.getUserFormControl();
			if (control != null) {
				FormControl controlObject = control.newInstance();
				mform.setControl(controlObject);
			}
			vform.setForm(mform);
			vform.doBuild();
			vform.setSizeFull();
			
			mform.setBuilder(vform.getBuilder());
			if (mform.getControl() != null)
				mform.getControl().setup();
			
			return vform;
		} catch (Throwable t) {
			log.e(node,t);
		}
		return null;
	}

	@Override
	public void doAction(MForm form, String action) {
		if (action.startsWith("submit:")) {
			action = action.substring(7);
			MProperties p = dataSource.getProperties();
			p.putAll(MProperties.explodeToMProperties(action));
			onFormSubmit(node, p);
		} else
		if (action.startsWith("action:")){
			action = action.substring(7);
			MProperties p = dataSource.getProperties();
			p = onAction(node, p, action);
			if (p != null) {
				dataSource.getProperties().putAll(p);
				vForm.getBuilder().doUpdateValues();
			}
		} else
			log.w("Unknown action type " + action );
	}

}
