package de.mhus.cherry.reactive.engine.ui;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.mhus.cherry.reactive.model.ui.IEngineClassLoader;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;

public class UiFormInformation implements IFormInformation, Externalizable {

    private DefRoot form;
    private Class<? extends ActionHandler> actionHandler;
    private Class<? extends FormControl> formControl;

    
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
        out.writeObject(form); //??? totring
        out.writeUTF(actionHandler.getClass().getCanonicalName());
        out.writeUTF(formControl.getClass().getCanonicalName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if ( in.readInt() != 1) throw new IOException("Wrong object version");
        form = (DefRoot) in.readObject(); //??? fromString
        {
            String name = in.readUTF();
            actionHandler = (Class<? extends ActionHandler>) IEngineClassLoader.instance().load(name);
        }
        {
            String name = in.readUTF();
            formControl = (Class<? extends FormControl>) IEngineClassLoader.instance().load(name);
        }
    }

}
