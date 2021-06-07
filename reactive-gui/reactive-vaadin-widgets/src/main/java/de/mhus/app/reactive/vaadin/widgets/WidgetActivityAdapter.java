package de.mhus.app.reactive.vaadin.widgets;

import java.util.UUID;

public class WidgetActivityAdapter implements WidgetActivityDelegate {

    @Override
    public void showNodeDetails(UUID id) {
        
    }

    @Override
    public void showForm(UUID id) {
        
    }

    @Override
    public void showCaseDetails(UUID id) {
        
    }

    @Override
    public void showCaseRuntime(UUID id) {
        
    }

    @Override
    public void showNodeRuntime(UUID id) {
        
    }

    @Override
    public void doCaseArchive(UUID id) {
        
    }

    @Override
    public boolean isShowNodeAssign(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeDetails(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeRuntime(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeRefresh(UUID id) {
        return true;
    }

    @Override
    public boolean isShowNodeDue(UUID id) {
        return true;
    }

    @Override
    public void doDue(UUID id) {
    }

    @Override
    public boolean isShowCaseDetails(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseActions(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseRefresh(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseRuntime(UUID id) {
        return true;
    }

    @Override
    public boolean isShowCaseArchive(UUID id) {
        return true;
    }

}
