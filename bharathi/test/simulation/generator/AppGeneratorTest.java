package simulation.generator;

import org.griphyn.vdl.dax.ADAG;
import org.griphyn.vdl.dax.Job;
import org.junit.jupiter.api.Test;
import simulation.generator.app.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

        Application app = new CyberShake();

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

    private LongStream getPeakMems(ADAG dax){
        Iterable iterable = dax::iterateJob;
        Stream<AppJob> targetStream = StreamSupport.stream(iterable.spliterator(), false);
        return targetStream.mapToLong(j -> Long.parseLong(j.getAnnotation("peak_mem_bytes")));
    }

    @Test
    void generateSipht() throws Exception{
        SIPHT sipht = new SIPHT();
        sipht.generateWorkflow("-n", ""+10000);
        sipht.printWorkflow(System.out);

    }
    @Test
    void generateWorkflows() throws Exception {

        Locale.setDefault(new Locale("EN_us")); //Locale.setDefault();//setDefault(new Locale());

        Application[] applications = {
                new CyberShake(),
                new LIGO(),
//                new Montage(),
//                new SIPHT(),
        };

        for(Integer numTasks : new int[]{50,500,600,800,1000,2000}) {
            for (Application app : applications) {

                // generate dax
                app.generateWorkflow("-n", numTasks.toString());

                System.out.println("app = " + app.getClass().getSimpleName());
                System.out.printf("min peak mem = %s MB%n", getPeakMems(app.getDAX()).min().getAsLong() / 1e6);
                System.out.printf("max peak mem = %s MB%n", getPeakMems(app.getDAX()).max().getAsLong() / 1e6);
                // write to text file
                String filename = String.format("results/%s.n.%d.0.dax.new3", app.getClass().getSimpleName(), numTasks);
                FileOutputStream fop = new FileOutputStream(new File(filename));
                app.printWorkflow(fop);
                fop.close();

                // write out memory distributions
                if(numTasks==2000){
                    FileWriter fileWriter = new FileWriter("evaluation/sampled-peak-mem-"+app.getClass().getSimpleName()+".csv");

                    Iterable iterable = app.getDAX()::iterateJob;
                    Stream<AppJob> targetStream = StreamSupport.stream(iterable.spliterator(), false);
                    fileWriter.write(String.format("task_type,input_size_total_bytes,peak_mem_bytes\n"));
                    targetStream.forEach(j -> {
                        try {
                            fileWriter.write(String.format("%s,%s,%s%n",j.getName(),j.getAnnotation("input_total_bytes"),j.getAnnotation("peak_mem_bytes")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    fileWriter.close();
                }
            }

        }
    }


}