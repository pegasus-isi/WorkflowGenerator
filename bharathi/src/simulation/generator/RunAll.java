package simulation.generator;

import java.io.File;
import java.io.FileOutputStream;

import simulation.generator.app.*;
import simulation.generator.app.Ligo;

/**
 * Generate several workflows for each application.
 * 
 * @author Gideon Juve <juve@usc.edu>
 */
class RunAll {
    private static void run(Application app, File outfile, String... args) throws Exception {
        app.generateWorkflow(args);
        app.printWorkflow(new FileOutputStream(outfile));
    }
    
    public static void main(String[] args) throws Exception {
        run(new Cybershake(), new File("CyberShake_30.xml"), "-n", "30");
        run(new Cybershake(), new File("CyberShake_50.xml"), "-n", "50");
        run(new Cybershake(), new File("CyberShake_100.xml"), "-n", "100");
        run(new Cybershake(), new File("CyberShake_1000.xml"), "-n", "1000");
        
        run(new Montage(), new File("Montage_25.xml"), "-n", "25");
        run(new Montage(), new File("Montage_50.xml"), "-n", "50");
        run(new Montage(), new File("Montage_100.xml"), "-n", "100");
        run(new Montage(), new File("Montage_1000.xml"), "-n", "1000");
        
        run(new Genome(), new File("Epigenomics_24.xml"), "-n", "24");
        run(new Genome(), new File("Epigenomics_46.xml"), "-n", "46");
        run(new Genome(), new File("Epigenomics_100.xml"), "-n", "100");
        run(new Genome(), new File("Epigenomics_997.xml"), "-n", "997");
        
        run(new Ligo(), new File("Inspiral_30.xml"), "-n", "30");
        run(new Ligo(), new File("Inspiral_50.xml"), "-n", "50");
        run(new Ligo(), new File("Inspiral_100.xml"), "-n", "100");
        run(new Ligo(), new File("Inspiral_1000.xml"), "-n", "1000");
        
        run(new SIPHT(), new File("Sipht_30.xml"), "-n", "30");
        run(new SIPHT(), new File("Sipht_60.xml"), "-n", "60");
        run(new SIPHT(), new File("Sipht_100.xml"), "-n", "100");
        run(new SIPHT(), new File("Sipht_1000.xml"), "-n", "1000");
    }
}
