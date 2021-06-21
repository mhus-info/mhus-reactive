package de.mhus.app.reactive.examples.demo.v1_0_0;

import org.osgi.service.component.annotations.Component;

import de.mhus.app.reactive.model.activity.AProcess;
import de.mhus.app.reactive.model.annotations.ProcessDescription;
import de.mhus.app.reactive.util.bpmn2.RProcess;

@Component(service = AProcess.class)
@ProcessDescription(version = "1.0.0")
public class DemoProcess extends RProcess {}
