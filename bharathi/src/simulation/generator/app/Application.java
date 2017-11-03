package simulation.generator.app;

import java.io.OutputStream;

import org.griphyn.vdl.dax.ADAG;

/**
 * @author Shishir Bharathi
 * @author Gideon Juve <juve@usc.edu>
 */
public interface Application {
    ADAG getDAX();
    void generateWorkflow(String... args);
    void printWorkflow(OutputStream os) throws Exception;
}
