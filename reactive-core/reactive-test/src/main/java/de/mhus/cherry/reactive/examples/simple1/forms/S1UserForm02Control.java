package de.mhus.cherry.reactive.examples.simple1.forms;

import de.mhus.lib.core.MLog;
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.MForm;
import de.mhus.lib.form.UiComponent;

public class S1UserForm02Control extends MLog implements FormControl {

	private MForm form;

	@Override
	public void attachedForm(MForm form) {
		this.form = form;
	}

	@Override
	public void focus(UiComponent component) {
		
	}

	@Override
	public boolean newValue(UiComponent component, Object newValue) {
		
		if (component.getName().equals("ctext1")) {
			try {
				UiComponent c = form.getBuilder().getComponent("ctext2");
				form.getDataSource().setObject(c, DataSource.VALUE, newValue);
				c.doUpdateValue();
			} catch (Throwable t) {
				log().e(component,newValue,t);
			}
		} else
		if (component.getName().equals("ctext2")) {
			try {
				UiComponent c = form.getBuilder().getComponent("ctext1");
				form.getDataSource().setObject(c, DataSource.VALUE, newValue);
				c.doUpdateValue();
			} catch (Throwable t) {
				log().e(component,newValue,t);
			}
		} else
		if (component.getName().equals("cgender")) {
			String v = String.valueOf(newValue);
			UiComponent vMale = form.getBuilder().getComponent("cmale");
			UiComponent vFemale = form.getBuilder().getComponent("cfemale");
			try {
				vMale.setVisible("MR".equals(v));
				vFemale.setVisible("MRS".equals(v));
			} catch (Throwable t) {
				log().e(component,newValue,t);
			}
		}
			
		return true;
	}

	@Override
	public void reverted(UiComponent component) {
		
	}

	@Override
	public void newValueError(UiComponent component, Object newValue, Throwable t) {
		
	}

	@Override
	public void valueSet(UiComponent component) {
		
	}

	@Override
	public void setup() {
		
		try {
			UiComponent vGender = form.getBuilder().getComponent("cgender");
			String v = form.getDataSource().getString(vGender, DataSource.VALUE, "");
			UiComponent vMale = form.getBuilder().getComponent("cmale");
			UiComponent vFemale = form.getBuilder().getComponent("cfemale");
			vMale.setVisible("MR".equals(v));
			vFemale.setVisible("MRS".equals(v));
		} catch (Throwable t) {
			log().e(t);
		}
	}

}
