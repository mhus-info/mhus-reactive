package de.mhus.cherry.reactive.model.migrate;

public interface Migrator {

	void doMigrate(MigrationContext context);

}
