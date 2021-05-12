package de.mhus.app.reactive.model.util;

import java.util.Set;

import de.mhus.app.reactive.model.engine.EngineConst;
import de.mhus.app.reactive.model.ui.IEngine;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.form.DefaultFormInformation;
import de.mhus.lib.form.IFormInformation;
import de.mhus.lib.form.ModelUtil;

public class CaseActionList {

    private IEngine engine;
    private String caseId;
    private MProperties list;
    
    public CaseActionList() {}
    
    public CaseActionList(IEngine engine, String caseId) {
        this.engine = engine;
        this.caseId = caseId;
        
        try {
            list = engine.onUserCaseAction(caseId, EngineConst.ACTION_LIST, null);
            if (list == null || list.size() == 0) {
                list = null;
                return;
            }
        } catch (Throwable t) {
            t.printStackTrace(); // TODO
            list = null;
        }
    }

    public Set<String> getNames() {
        return list.keys();
    }

    public String getTitle(String action) {
        String desc = list.getString(action, null);
        if (desc == null || desc.length() == 0) return null;
        return desc;
    }

    public IFormInformation getForm(String action) {
        String desc = list.getString(action, null);
        if (desc == null || desc.length() == 0) return null;
        try {
            MProperties values = new MProperties();
            values.put("action", action);
            MProperties ret = engine.onUserCaseAction(caseId, EngineConst.ACTION_FORM, values);
            if (ret == null || !ret.containsKey("form"))
                return null;
            DefRoot root = ModelUtil.fromJson(ret.getString("form", ""));
            return new DefaultFormInformation(root, null, null);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public MProperties onCaseAction(String action, MProperties properties) throws Exception {
        return engine.onUserCaseAction(caseId, action, properties);
    }

    public boolean isValid() {
        return list != null;
    }

}
