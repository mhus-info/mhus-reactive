package de.mhus.app.reactive.examples.simple1;

import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;
import de.mhus.lib.form.definition.FaShowInformationPanel;
import de.mhus.lib.form.definition.FmText;

public class S1PoolTestForm implements IFormInformation {

    @Override
    public DefRoot getForm() {
        return new DefRoot( 
            new FaShowInformationPanel(),
            new FmText("name", "Name", "Insert the name"),
            new FmText("desc", "Description", "Insert description")
        );
    }

    @Override
    public Class<? extends ActionHandler> getActionHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends FormControl> getFormControl() {
        // TODO Auto-generated method stub
        return null;
    }

}
