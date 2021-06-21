package de.mhus.app.reactive.util.bpmn2;

import java.util.Date;
import java.util.jar.Manifest;

import de.mhus.app.reactive.model.activity.AProcess;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.errors.NotFoundException;

public class RProcess extends MLog implements AProcess {

    @Override
    public String getVersionInformation() {
        try {
            Manifest manifest = MSystem.getManifest(getClass());
            String bundleVersion = manifest.getMainAttributes().getValue("Bundle-Version");
            String bundleSymbolicName = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
            long bndLastModified = MCast.tolong(manifest.getMainAttributes().getValue("Bnd-LastModified"), 0);
            return bundleSymbolicName + ":" + bundleVersion 
                    + (bndLastModified <= 0 ? "" : " (" + new Date(bndLastModified) + ")");
        } catch (NotFoundException e) {}
        return null;
    }

    @Override
    public long getBuildTime() {
        try {
            Manifest manifest = MSystem.getManifest(getClass());
            long bndLastModified = MCast.tolong(manifest.getMainAttributes().getValue("Bnd-LastModified"), 0);
            return bndLastModified;
        } catch (NotFoundException e) {}
        return 0;
    }

}
