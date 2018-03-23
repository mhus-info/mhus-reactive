package de.mhus.cherry.reactive.util.engine;

import java.util.UUID;

import de.mhus.lib.core.config.NodeConfig;
import de.mhus.lib.errors.MException;
import de.mhus.lib.sql.DbPool;
import de.mhus.lib.sql.DbPoolBundle;

public class MemoryStorage extends SqlDbStorage {

	public MemoryStorage() throws MException {
		super(createPool(), "db");
	}

	private static DbPool createPool() throws MException {
		
		String name = UUID.randomUUID().toString();
		
		String jdbcDriver = "org.hsqldb.jdbcDriver";
		String jdbcUrl = "jdbc:hsqldb:mem:" + name;
		String jdbcUser = "sa";
		String jdbcPass = "";

		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException e) {
			throw new MException("HSQLDB driver not found",jdbcDriver);
		}

		NodeConfig cdb = new NodeConfig();
		NodeConfig cconfig = new NodeConfig();

		cdb.setProperty("driver", jdbcDriver);
		cdb.setProperty("url", jdbcUrl);
		cdb.setProperty("user", jdbcUser);
		cdb.setProperty("pass", jdbcPass);

		cconfig.setConfig("db", cdb);
//		MActivator activator = new DefaultActivator(MemoryStorage.class.getClassLoader());
		DbPoolBundle bundle = new DbPoolBundle(cconfig,null);

		try {
			return bundle.getPool("db");
		} catch (Exception e) {
			throw new MException("can't create pool",e);
		}
	}

}
