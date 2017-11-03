package simulation.generator.app;

import org.griphyn.vdl.dax.ADAG;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.HashMap;
import simulation.generator.util.Distribution;

/**
 * @author Shishir Bharathi
 */
public abstract class AbstractApplication implements Application {
    
    private final ADAG dax;
    private int id;
    final Map<String, Distribution> distributions;
    
    AbstractApplication() {
        this.dax = new ADAG();
        this.id = 0;
        this.distributions = new HashMap<String, Distribution>();
    }
    
    protected Map<String, Distribution> getDistributions() {
        return this.distributions;
    }

    double generateDouble(String key) {
        Distribution dist = this.distributions.get(key);
        if (dist == null) {
            throw new RuntimeException("No such distribution: "+key);
        }
        return dist.getDouble();
    }

    long generateLong(String key) {
        return (long) generateDouble(key);
    }

    int generateInt(String key) {
        return (int) generateDouble(key);
    }

    protected abstract void populateDistributions();
    
    String getNewJobID() {
        return String.format("ID%05d", this.id++);
    }
    
    @Override
    public void printWorkflow(OutputStream os) throws Exception {
        this.dax.toXML(new OutputStreamWriter(os), "", null);
    }
    
    public ADAG getDAX() {
        return this.dax;
    }
    
    public void generateWorkflow(String... args) {
        populateDistributions();
        processArgs(args);
        constructWorkflow();
    }
    
    protected abstract void processArgs(String[] args);
    protected abstract void constructWorkflow();
}
