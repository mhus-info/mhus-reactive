package de.mhus.cherry.reactive.vaadin.widgets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.MForm;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.vaadin.form.VaadinForm;

public class VHumanForm extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private INode node;
	private Button bCancel;
	private PropertiesDataSource dataSource;

	public VHumanForm(INode node) {
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
			HumanForm hForm = node.getHumanForm();
			DefRoot form = hForm.getRoot();
			dataSource = new PropertiesDataSource();
			dataSource.setProperties(new MProperties( node.getHumanFormValues()));
			
			VaadinForm vform = new VaadinForm();
			vform.setShowInformation(true);
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
