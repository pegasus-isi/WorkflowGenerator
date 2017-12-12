package simulation.generator;

import org.griphyn.vdl.dax.Job;
import org.junit.jupiter.api.Test;
import simulation.generator.app.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Carl Witt on 03.11.17.
 *
 * The dax.new2 format added relative time to failure values (I think).
 * The dax.new3 format switched from a uniform error model to a normally distributed error model.
 *
 */
class AppGeneratorTest {

    @Test
    void main() throws Exception {

        // avoid setting getting comma decimal separators for German locale
        Locale.setDefault(new Locale("EN_us")); //Locale.setDefault();//setDefault(new Locale());

        Application app = new Cybershake();

        String[] args = new String[]{
//            "--data", "100",                   // -d Approximate size of input data.
//            "--factor", "100",                 // -f Avg. runtime to execute an mProject job.
//            "--inputs", "100",                 // -i Number of inputs.
                "-n", "20",                // -n Number of jobs.
//            "--overlap-probability", "0.5",    // -p Probability any two inputs overlap.
//            "--square", "100"                  // -s Square degree of workflow.
        };

        app.generateWorkflow(args);

        Iterator iterator = app.getDAX().iterateJob();
        while (iterator.hasNext()) {
            Job next = (Job) iterator.next();
            System.out.printf("job %s\n%s%n", next.getName(), next.getArgument(0));
        }
//        app.printWorkflow(System.out);
    }

    @Test
    void generateYarnStarvationTest() throws Exception {

        for(String minute : new String[]{"3", "25", "500"}){

            // generate workflow
            YarnStarvationTest test = new YarnStarvationTest();
            test.generateWorkflow(minute);

            // write to file
            String filename = String.format("results/yarn-starvation-test/yarn-starvation-v3-%s-minutes.dax", minute);
            FileOutputStream fop = new FileOutputStream(new File(filename));
            test.printWorkflow(fop);
            fop.close();
        }

    }

    @Test
    void generateSipht() throws Exception{
        SIPHT sipht = new SIPHT();
        sipht.generateWorkflow("-n", "400");
        sipht.printWorkflow(System.out);

    }
    @Test
    void generateWorkflows() throws Exception {

        String targetDir = "results/new4/";

        // avoid mixing up commas and dots when converting floating points to string (german vs. english locales)
        Locale.setDefault(new Locale("EN_us")); //Locale.setDefault();//setDefault(new Locale());

        AbstractApplication[] applications = {
                new Cybershake(),
                new Ligo(),
//                new Montage(),
//                new SIPHT(),
        };

        // contains the csv line per workflow file
        StringBuffer workflowStatisticsCsv = new StringBuffer();
        workflowStatisticsCsv.append("file,num_tasks,total_runtime_seconds,total_spacetime_megabyteseconds,minimum_peak_memory_mb,maximum_peak_memory_mb\n");


        for(Integer numTasks : new int[]{50,500,600,800,1000,2000}) {
            for (AbstractApplication app : applications) {

                // generate dax
                app.generateWorkflow("-n", numTasks.toString());

                AbstractApplication.WorkflowStatistics statistics = app.getStatistics();

                // write to text file
                String filename = String.format("%s.n.%d.0.dax.new3", app.getClass().getSimpleName(), statistics.numberOfTasks);
                FileOutputStream fop = new FileOutputStream(new File(targetDir +filename));
                app.printWorkflow(fop);
                fop.close();

                workflowStatisticsCsv.append(String.format("%s,%s,%s,%s,%s,%s\n",filename, statistics.numberOfTasks, statistics.totalRuntimeSeconds, statistics.totalSpacetimeMegabyteSeconds, 1e-6*statistics.minimumPeakMemory, 1e-6*statistics.maximumPeakMemoryBytes));

                // write out memory distributions
//                if(numTasks==2000){
//                    FileWriter fileWriter = new FileWriter("evaluation/sampled-peak-mem-"+app.getClass().getSimpleName()+".csv");
//
//                    Iterable iterable = app.getDAX()::iterateJob;
//                    Stream<AppJob> targetStream = StreamSupport.stream(iterable.spliterator(), false);
//                    fileWriter.write(String.format("task_type,input_size_total_bytes,peak_mem_bytes\n"));
//                    targetStream.forEach(j -> {
//                        try {
//                            fileWriter.write(String.format("%s,%s,%s%n",j.getName(),j.getAnnotation("input_total_bytes"),j.getAnnotation("peak_mem_bytes")));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                    fileWriter.close();
//                }
            }
        }
        Files.write(Paths.get(targetDir+"/workflowStatistics.csv"), workflowStatisticsCsv.toString().getBytes());
    }


}