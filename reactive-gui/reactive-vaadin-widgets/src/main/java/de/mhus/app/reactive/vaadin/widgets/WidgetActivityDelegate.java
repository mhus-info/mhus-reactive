package de.mhus.app.reactive.vaadin.widgets;

import java.util.UUID;

public interface WidgetActivityDelegate {


    void showNodeDetails(UUID id);

    void showForm(UUID id);

    void showCaseDetails(UUID id);

    void showCaseRuntime(UUID id);

    void showNodeRuntime(UUID id);
    
    void doCaseArchive(UUID id);

    boolean isShowNodeAssign(UUID id);

    boolean isShowNodeDetails(UUID id);

    boolean isShowNodeRuntime(UUID id);

    boolean isShowNodeRefresh(UUID id);

    boolean isShowNodeDue(UUID id);

    void doDue(UUID id);

    boolean isShowCaseDetails(UUID id);

    boolean isShowCaseActions(UUID id);

    boolean isShowCaseRefresh(UUID id);

    boolean isShowCaseRuntime(UUID id);

    boolean isShowCaseArchive(UUID id);

}
