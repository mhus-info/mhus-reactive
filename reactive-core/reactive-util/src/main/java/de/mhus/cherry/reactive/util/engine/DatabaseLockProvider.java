package de.mhus.cherry.reactive.util.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.CaseLockProvider;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.TimeoutException;
import de.mhus.lib.sql.DataSourceProvider;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbResult;
import de.mhus.lib.sql.DbStatement;

public class DatabaseLockProvider extends MLog implements CaseLockProvider {

    private DataSourceProvider ds;
    private String table;
    private String key;

    public DatabaseLockProvider(DataSourceProvider dsProvider, String tableName, String keyField) {
        this.ds = dsProvider;
        this.table = tableName;
        this.key = keyField;
    }
    
    @Override
    public boolean isCaseLocked(UUID caseId) {
        try (DbConnection con = ds.createConnection()) {
            DbStatement sth = con.createStatement("SELECT "+key+" FROM " + table + " WHERE " + key + "=$key$ FOR UPDATE NOWAIT");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("key", "case_" + caseId);
            sth.executeQuery(attributes);
            return false;
        } catch (Exception e) {
            log().d(e);
            return true;
        }
    }

    @Override
    public Lock lock(UUID caseId) throws TimeoutException {
        while (true) {
            Con con = tryLock("case_" + caseId);
            if (con != null)
                return new DbLock(con, caseId.toString());
            MThread.sleep(200);
        }
    }
    
    private Con tryLock(String value) {
        DbConnection con = null;
        try {
            con = ds.createConnection();
            DbStatement sth = con.createStatement("SELECT "+key+" FROM " + table + " WHERE " + key + "=$key$ FOR UPDATE NOWAIT");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("key", value);
            DbResult res = sth.executeQuery(attributes);
            if (res.next()) {
                res.close();
                return new Con(con,sth);
            }
            res.close();
            
            DbStatement sthSet = con.createStatement("INSERT INTO " + table + "(" + key+") VALUES ($key$)");
            boolean done = sthSet.execute(attributes);
            if (!done) {
                con.close();
                return null;
            }
            sthSet.close();
            con.commit();
            
            res = sth.executeQuery(attributes);
            if (res.next()) {
                res.close();
                return new Con(con,sth);
            }
            res.close();
            sth.close();
            con.commit();
            con.close();
            return null;
        } catch (Exception e) {
            log().d(e);
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    log().e(e1);
                }
                con.close();
            }
            return null;
        }

    }

    @Override
    public Lock acquireCleanupMaster() {
        synchronized (this) {
            Con masterCleanup = tryLock("master_cleaup");
            if (masterCleanup == null) return null;
            return new DbLock(masterCleanup,"master_cleanup");
        }
    }

    @Override
    public Lock acquirePrepareMaster() {
        synchronized (this) {
            Con masterPrepare = tryLock("master_cleaup");
            if (masterPrepare == null) return null;
            return new DbLock(masterPrepare, "master_cleanup");
        }
    }

    @Override
    public Lock acquireEngineMaster() {
        synchronized (this) {
            while (true) {
                Con masterEngine = tryLock("master_cleaup");
                if (masterEngine != null)
                    return new DbLock(masterEngine, "master_cleanup");
                MThread.sleep(300);
            }
        }
    }

//    @Override
//    public void releaseEngineMaster() {
//        synchronized (this) {
//            if (masterEngine != null)
//                masterEngine.close();
//            masterEngine = null;
//        }
//    }

    @Override
    public Lock lockOrNull(UUID caseId) {
        Con con = tryLock("case_" + caseId);
        if (con == null) return null;
        return new DbLock(con, caseId.toString());
    }

    @Override
    public boolean isReady() {
        return true; // ?
    }

    private class Con {
        DbConnection con;
        DbStatement sth;
        
        public Con(DbConnection con, DbStatement sth) {
            this.con = con;
            this.sth = sth;
        }

        public void close() {
            if (sth == null) return;
            sth.close();
            try {
                con.commit();
            } catch (Exception e) {
                log().e(e);
            }
            con.close();
            sth = null;
            con = null;
        }
    }
    
    private class DbLock implements Lock {

        private Con con;
        private String name;

        public DbLock(Con con, String name) {
            this.con = con;
            this.name = name;
        }

        @Override
        public Lock lock() {
            return this;
        }

        @Override
        public boolean lock(long timeout) {
            return con != null;
        }

        @Override
        public synchronized boolean unlock() {
            if (con == null) return true;
            con.close();
            return true;
        }

        @Override
        public void unlockHard() {
            unlock();
        }

        @Override
        public boolean isLocked() {
            return con != null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOwner() {
            return "";
        }

        @Override
        public long getLockTime() {
            return 0;
        }

        @Override
        public boolean refresh() {
            return false;
        }

        @Override
        public long getCnt() {
            return 0;
        }

        @Override
        public String getStartStackTrace() {
            return null;
        }
        
    }
}
