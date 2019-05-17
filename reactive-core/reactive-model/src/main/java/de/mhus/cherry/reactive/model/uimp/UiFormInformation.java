package de.mhus.cherry.reactive.model.uimp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.mhus.cherry.reactive.model.ui.IEngineClassLoader;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;
import de.mhus.lib.form.ModelUtil;

public class UiFormInformation implements IFormInformation, Externalizable {

    private static final long serialVersionUID = 1L;
    private DefRoot form;
    private Class<? extends ActionHandler> actionHandler;
    private Class<? extends FormControl> formControl;

    
    public UiFormInformation() {}
    
    public UiFormInformation(DefRoot form, Class<? extends ActionHandler> actionHandler,
            Class<? extends FormControl> formControl) {
        this.form = form;
        this.actionHandler = actionHandler;
        this.formControl = formControl;
    }

    @Override
    public DefRoot getForm() {
        return form;
    }

    @Override
    public Class<? extends ActionHandler> getActionHandler() {
        return actionHandler;
    }

    @Override
    public Class<? extends FormControl> getFormControl() {
        return formControl;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(1);
        try {
            if (form != null) {
                form.build();
                String formXml = MXml.toString(ModelUtil.toXml(form), false);
                out.writeObject(formXml); 
            } else {
                out.writeObject(null);
            }
        } catch (Exception e) {
            MLogUtil.log().e(getClass(),e);
            out.writeObject(null);
        }
        if (actionHandler != null)
            out.writeObject(actionHandler.getClass().getCanonicalName());
        else
            out.writeObject(null);
        if (formControl != null)
            out.writeObject(formControl.getClass().getCanonicalName());
        else
            out.writeObject(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if ( in.readInt() != 1) throw new IOException("Wrong object version");
        String formXml = (String) in.readObject();
        if (formXml != null) {
            try {
                form = ModelUtil.toModel( MXml.loadXml(formXml).getDocumentElement() );
            } catch (Exception e) {
                MLogUtil.log().e(getClass(),e);
            }
        }
        {
            String name = (String) in.readObject();
            if (name != null)
                actionHandler = (Class<? extends ActionHandler>) IEngineClassLoader.instance().load(name);
        }
        {
            String name = (String) in.readObject();
            if (name != null)
                formControl = (Class<? extends FormControl>) IEngineClassLoader.instance().load(name);
        }
    }

}
