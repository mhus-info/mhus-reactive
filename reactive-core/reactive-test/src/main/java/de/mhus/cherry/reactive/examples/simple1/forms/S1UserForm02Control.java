package de.mhus.cherry.reactive.examples.simple1.forms;

import java.util.Date;

import de.mhus.lib.core.MDate;
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.FormControlAdapter;
import de.mhus.lib.form.UiComponent;

public class S1UserForm02Control extends FormControlAdapter {

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
			
		return super.newValue(component, newValue);
	}

	@Override
	public void setup() {
		super.setup();
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

	@Override
	public void doAction(String action, Object... params) {
		if (action.equals("now")) {
			UiComponent v = form.getBuilder().getComponent("cnowtext");
			try {
				form.getDataSource().setObject(v, DataSource.VALUE, MDate.toDateTimeSecondsString(new Date()));
				v.doUpdateValue();
				return;
			} catch (Throwable t) {
				log().e(t);
			}
		}
		super.doAction(action, params);
	}

}
