package simulation.generator.app;

import org.griphyn.vdl.dax.ADAG;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.HashMap;
import simulation.generator.util.Distribution;
import simulation.generator.util.MemoryModel;

/**
 * @author Shishir Bharathi
 */
public abstract class AbstractApplication implements Application {

    /** the width of the interval of a uniform distribution with a known standard deviation sd is sqrt(12)*sd. */
    protected final double sqrt12 = Math.sqrt(12.);

    private final ADAG dax;
    private int id;
    final Map<String, Distribution> distributions = new HashMap<>();
    final Map<String, MemoryModel> memoryModels = new HashMap<>();

    AbstractApplication() {
        this.dax = new ADAG();
        this.id = 0;
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
