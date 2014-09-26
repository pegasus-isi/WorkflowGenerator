package simulation.generator.app;

import java.io.OutputStream;

import org.griphyn.vdl.dax.ADAG;

/**
 * @author Shishir Bharathi
 * @author Gideon Juve <juve@usc.edu>
 */
public interface Application {
    public ADAG getDAX();
    public void generateWorkflow(String... args) throws Exception;
    public void printWorkflow(OutputStream os) throws Exception;
}
