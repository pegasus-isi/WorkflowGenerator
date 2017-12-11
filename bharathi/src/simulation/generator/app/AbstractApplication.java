package simulation.generator.app;

import org.griphyn.vdl.dax.ADAG;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import simulation.generator.util.Distribution;
import simulation.generator.util.MemoryModel;

/**
 * @author Shishir Bharathi
 */
public abstract class AbstractApplication implements Application {

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

    public static class WorkflowStatistics{
        /** The number of jobs (=tasks=vertices in the DAG) in the workflow. */
        public int numberOfTasks;
        /** The accumulated runtime of all tasks in seconds. */
        public double totalRuntimeSeconds;
        /** The accumulated megabyteseconds (runtime * peak memory consumption) of all tasks. */
        public double totalSpacetimeMegabyteSeconds;

        public long minimumPeakMemory = Long.MAX_VALUE;
        public long maximumPeakMemoryBytes = 0L;
//        public Map<String, Long> minimumPeakMemoryByTaskType;

    }

    private LongStream getPeakMems(ADAG dax){
        Iterable iterable = dax::iterateJob;
        Stream<AppJob> targetStream = StreamSupport.stream(iterable.spliterator(), false);
        return targetStream.mapToLong(j -> Long.parseLong(j.getAnnotation("peak_mem_bytes")));
    }

    public WorkflowStatistics getStatistics(){
        WorkflowStatistics statistics = new WorkflowStatistics();

        Iterator<AppJob> iterable = getDAX().iterateJob();

        while (iterable.hasNext()) {

            AppJob next =  iterable.next();

            // count number of tasks
            statistics.numberOfTasks++;

            // accumulate total task runtime
            double taskRuntimeSeconds = Double.parseDouble(next.getAnnotation("runtime"));
            assert taskRuntimeSeconds > 0;
            statistics.totalRuntimeSeconds += taskRuntimeSeconds;

            // accumulate total task spacetime usage
            long taskMemoryBytes = Long.parseLong(next.getAnnotation("peak_mem_bytes"));
            double taskSpacetimeMegabyteSeconds = 1e-6* taskMemoryBytes;
            assert taskSpacetimeMegabyteSeconds > 0;
            statistics.totalSpacetimeMegabyteSeconds += taskRuntimeSeconds * taskSpacetimeMegabyteSeconds;

            // find minimum and maximum peak memory usage across tasks
            statistics.maximumPeakMemoryBytes = Math.max(statistics.maximumPeakMemoryBytes, taskMemoryBytes);
            statistics.minimumPeakMemory= Math.min(statistics.minimumPeakMemory, taskMemoryBytes);

        }
        return statistics;
    }
    
    protected abstract void processArgs(String[] args);
    protected abstract void constructWorkflow();


}
