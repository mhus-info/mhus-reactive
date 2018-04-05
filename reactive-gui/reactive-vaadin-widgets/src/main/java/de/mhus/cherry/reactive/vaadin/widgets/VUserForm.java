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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.util.UserForm;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.MForm;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.vaadin.form.VaadinForm;

public class VUserForm extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private INode node;
	private Button bCancel;
	private PropertiesDataSource dataSource;

	public VUserForm(INode node) {
		this.node = node;
		VaadinForm form = createForm();
		addComponent(form);
		setExpandRatio(form, 1);
		// add buttons
		HorizontalLayout toolBar = new HorizontalLayout();
		addComponent(toolBar);
		
		bCancel = new Button("Cancel");
		toolBar.addComponent(bCancel);
		bCancel.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				onFormCancel();
			}
			
		});
		
		Button bSubmit = new Button("Submit");
		toolBar.addComponent(bSubmit);
		bSubmit.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				onFormSubmit(node, dataSource.getProperties());
			}
			
		});

	}

	protected void onFormSubmit(INode node2, MProperties properties) {
		
	}

	protected void onFormCancel() {
		
	}

	protected VaadinForm createForm() {
		try {
			UserForm hForm = node.getUserForm();
			DefRoot form = hForm.getRoot();
			dataSource = new PropertiesDataSource();
			dataSource.setProperties(new MProperties( node.getUserFormValues()));
			
			VaadinForm vform = new VaadinForm();
//			vform.setShowInformation(true);
			MForm mform = new MForm(form);
			mform.setDataSource(dataSource);
			vform.setForm(mform);
			vform.doBuild();
			
			return vform;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

}
