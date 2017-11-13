package simulation.generator;

import org.griphyn.vdl.dax.Job;
import org.junit.jupiter.api.Test;
import simulation.generator.app.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Carl Witt on 03.11.17.
 *
 * @author Carl Witt (cpw@posteo.de)
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
    void generateOneEach() throws Exception {

        Locale.setDefault(new Locale("EN_us")); //Locale.setDefault();//setDefault(new Locale());

        int numTasks = 50;
        String[] args = new String[]{"-n", ""+numTasks}; // -n Number of jobs.

        Application[] applications = {
                new CyberShake(),
                new LIGO(),
//                new Montage(),
//                new SIPHT(),
        };

        for(Application app : applications){

            // generate dax
            app.generateWorkflow(args);

            // write to text file
            String filename = String.format("results/%s.n.%d.0.dax.new2", app.getClass().getSimpleName(), numTasks);
            FileOutputStream fop = new FileOutputStream(new File(filename));
//            if (!file.exists()) file.createNewFile();
            app.printWorkflow(fop);
            fop.close();
        }

    }


}