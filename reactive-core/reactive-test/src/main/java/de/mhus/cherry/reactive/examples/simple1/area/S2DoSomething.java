package de.mhus.cherry.reactive.examples.simple1.area;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RTask;
import de.mhus.lib.core.MThread;

@ActivityDescription(outputs = {
        @Output(activity = S3LeaveArea.class),
        @Output(name="abord",activity = S1TheEnd.class),
        
        })
public class S2DoSomething extends RTask<S1Pool>{

    private static boolean inProgress = false;
    public static boolean failed = false;
    public static long sleepTime = 1000;

    @Override
    public String doExecute() throws Exception {
        if (inProgress) {
            failed = true;
            throw new Exception("That's not fair .... I'm not alone!");
        }
        inProgress = true;
        System.out.println("It's only me ... doing something");
        MThread.sleep(sleepTime);
        inProgress = false;
        return "abord".equals(getPool().getText2()) ? "abord" : null;
    }

}
