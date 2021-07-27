package de.mhus.app.reactive.vaadin.widgets;

import java.util.UUID;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import de.mhus.app.reactive.model.engine.EngineConst;
import de.mhus.app.reactive.model.util.CaseActionList;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.ActionHandler;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;
import de.mhus.lib.form.MForm;
import de.mhus.lib.form.MutableMForm;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.vaadin.ModalDialog;
import de.mhus.lib.vaadin.form.VaadinForm;

public class CaseActionDialog extends ModalDialog implements ActionHandler {

    private static final long serialVersionUID = 1L;
    private CaseActionList caseActions;
    private VerticalLayout layout;
    private PropertiesDataSource dataSource;
    private String formAction;
    private VaadinForm vForm;
    private WidgetActivityDelegate activity;

    public CaseActionDialog(CaseActionList actions, WidgetActivityDelegate activity) throws Exception {
        this.caseActions = actions;
        this.actions = new Action[] {CLOSE};
        this.activity = activity;
        setPack(true);
        initUI();
        setCaption("Actions");
    }

    @Override
    protected void initContent(VerticalLayout layout) throws Exception {
        this.layout = layout;
        for (String action : caseActions.getNames()) {
            String title = caseActions.getTitle(action);
            Button button = new Button(title, new Button.ClickListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(ClickEvent event) {
                    doCaseAction(action);
                }
            });
            button.setWidthFull();
            layout.addComponent(button);
        }
    }

    protected void doCaseAction(String action) {
        IFormInformation form = caseActions.getForm(action);
        if (form != null) {
            // show form
            layout.removeAllComponents();
            vForm = createForm(form);
            if (vForm == null) {
                Notification.show("Formular fehlt", Type.TRAY_NOTIFICATION); // should not happen
                close();
                return;
            }
            layout.addComponent(vForm);
            layout.setExpandRatio(vForm, 1);
            this.actions = new Action[] {OK,CLOSE};
            formAction = action;
            updateButtons();
            setPack(true);
        } else {
            doExecuteCaseAction(action);
        }
    }

    private void doExecuteCaseAction(String action) {
        MProperties properties = null;
        if (dataSource != null) {
            properties = dataSource.getProperties();
        }
        try {
            MProperties ret = caseActions.onCaseAction(action, properties);
            if (ret == null)
                Notification.show("Ergebnis wurde nicht gesendet", Type.TRAY_NOTIFICATION);
            else {
                String retAction = ret.getString(EngineConst.ACTION_RET_ACTION, null);
                String id = ret.getString(EngineConst.ACTION_RET_ID, null);
                close();
                if (retAction != null && activity != null && id != null) {
                    switch (retAction) {
                    case EngineConst.ACTION_RET_ACTION_CASE:
                        activity.showCaseDetails(UUID.fromString(id));
                        break;
                    case EngineConst.ACTION_RET_ACTION_NODE:
                        activity.showNodeDetails(UUID.fromString(id));
                        break;
                    case EngineConst.ACTION_RET_ACTION_FORM:
                        activity.showForm(UUID.fromString(id));
                        break;
                    }
                } else {
                    Notification.show("Ergebnis", ret.toString(), Type.HUMANIZED_MESSAGE);
                }
            }
       } catch (Throwable t) {
            Notification.show("Fehler bei der Ausführung", Type.ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    protected VaadinForm createForm(IFormInformation hForm) {
        try {
            DefRoot form = hForm.getForm();
            dataSource = new PropertiesDataSource();
            dataSource.setProperties(new MProperties());

            VaadinForm vform = new VaadinForm();
            //          vform.setShowInformation(true);
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
            // TODO
            t.printStackTrace();
        }
        return null;
    }    
    @Override
    protected boolean doAction(Action action) {
        if (action == OK) {
            doExecuteCaseAction(formAction);
        }
        return true;
    }

    @Override
    public void doAction(MForm form, String action) {
        
    }

}
