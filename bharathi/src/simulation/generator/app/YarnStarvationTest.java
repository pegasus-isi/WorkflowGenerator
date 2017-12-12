package simulation.generator.app;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import simulation.generator.util.Distribution;
import simulation.generator.util.MemoryModel;
import simulation.generator.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * A workflow to experiment with the YARN scheduler. The question is whether large resource requests (max memory allocation size)
 * starve on a busy cluster or how long we have to wait for a container in relation to its requested amount of memory.
 *
 * Builds a simple workflow that imitates a closed loop system where each task starts a similar task upon completion.
 * The tasks differ mostly in the amount of memory they request. We have
 * - three streaks of 20 seconds * (1|2|3)GB containers that are supposed to induce a basic (memory) load level on all nodes.
 * - three streaks of 45 seconds * (6|11|20)GB containers
 *
 * This amounts to a total memory requirement of 43 GB, which should give some interesting effects on two 20GB nodes.
 *
 * version 1 was using up to 20 GB pre streak
 *     int[] memoryGB = new int[]{1,2,3,6,11,20};
 *
 * but the large tasks starved the small ones (also because of the scheduler, which sorts by decreasing size to put the largest possible task in a container)
 *
 * version 2 was reducing the pressure by using at most 12 GB per streak but there seems to be a bug in the scheduler that makes progress impossible
 *     int[] memoryGB = new int[]{1,2,3,6,11,12};
 *
 * version 3 was using same task names per streak to allow testing the memory aware scheduler
 *
 * @author Carl Witt
 */
public class YarnStarvationTest extends AbstractApplication {

    public static final String NAMESPACE = "YarnStarvationTest";

    private int minutes = 25;

    private void usage(int exitCode) {
        String msg = "YarnStarvationTest <minutes to run>";
        System.out.println(msg);
        System.exit(exitCode);
    }

    @Override
    protected void processArgs(String[] args) {

        if(args.length > 0) minutes = Integer.parseInt(args[0]);

    }

    public void constructWorkflow() {

        int workflowRuntimeSeconds = minutes * 60;

        // 6 streaks of tasks, the first consists of a sequence of tasks with 2GB memory that run for 20 seconds, etc.
        int[] memoryGB = new int[]{1,2,3,6,11,12};
        int[] numTasks = new int[memoryGB.length];
        int[] runtimeSeconds = new int[]{20,20,20,45,45,45};

        for (int i = 0; i < numTasks.length; i++) {
            numTasks[i] = workflowRuntimeSeconds/runtimeSeconds[i];
        }

        // for each streak
        for (int streakId = 0; streakId <memoryGB.length; streakId++) {

            // all tasks in that streak
            AppJob[] streakTasks = new AppJob[numTasks[streakId]];

            // create the tasks in that streak
            for (int j = 0; j < streakTasks.length; j++) {

                String taskName = String.format("streak_%sGB", memoryGB[streakId], j); // _no%s

                // create task
                streakTasks[j] = new AppJob(this, NAMESPACE, taskName, "1.0", getNewJobID());
                // set runtime and memory consumption
                streakTasks[j].addAnnotation("runtime", String.format("%.2f", (double) runtimeSeconds[streakId]));
                streakTasks[j].addAnnotation("peak_mem_bytes", ""+(long)(memoryGB[streakId]*1e9));

                // add this task as a child to the previous task
                if(j>0) streakTasks[j-1].addLink(streakTasks[j],taskName+".in", 100);
            }
        }
    }


    @Override
    protected void populateDistributions() {
    }
}


