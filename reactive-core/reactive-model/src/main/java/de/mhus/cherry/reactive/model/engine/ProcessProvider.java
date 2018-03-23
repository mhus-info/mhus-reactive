package de.mhus.cherry.reactive.model.engine;

public interface ProcessProvider {

	EProcess getProcess(String name, String version);

	EProcess getProcess(String nameVersion);

}
