package de.mhus.app.reactive.vaadin.widgets;

import java.util.UUID;

public interface WidgetActivityDelegate {


    void showNodeDetails(UUID id);

    void showForm(UUID id);

    void showCaseDetails(UUID id);

    void showCaseRuntime(UUID id);

    void doCaseArchive(UUID id);

}
