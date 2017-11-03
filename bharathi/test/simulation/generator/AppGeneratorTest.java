package simulation.generator;

import org.junit.jupiter.api.Test;

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

        AppGenerator.main(new String[]{
                "-a", "MONTAGE",
//            "--data", "100",                   // -d Approximate size of input data.
//            "--factor", "100",                 // -f Avg. runtime to execute an mProject job.
//            "--inputs", "100",                 // -i Number of inputs.
                "-n", "20",                // -n Number of jobs.
//            "--overlap-probability", "0.5",    // -p Probability any two inputs overlap.
//            "--square", "100"                  // -s Square degree of workflow.
        });
    }

}