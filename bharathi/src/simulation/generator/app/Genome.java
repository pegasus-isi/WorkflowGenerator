package simulation.generator.app;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import simulation.generator.util.Distribution;
import simulation.generator.util.Misc;

/**
 * @author Shishir Bharathi
 */
public class Genome extends AbstractApplication {

    public static final String namespace = "Genome";
    private static final int MAX_SEQUENCES = 160;
    private static final int laneFactor = 4;
    private double runtimeFactor = 1;
    private String expt;
    private long referenceSize;
    private int[] counts;

    public String getNamespace() {
        return namespace;
    }

    public String getExpt() {
        return this.expt;
    }

    public long getReferenceSize() {
        return this.referenceSize;
    }

    private void usage(int exitCode) {
        String msg = "Genome [-h] [options]." +
                "\n--data | -d Approximate size of input data." + 
                "\n--expt | -e Experiment name." +
                "\n--factor | -f factor to scale runtimes." +
                "\n--help | -h Print help message." +
                "\n--lanes | -l Number of lanes." +
                "\n--numjobs | -n Number of jobs." +
                "\n--sequences | -s Number of sequences." +
                "\n\nOne of the following combinations is required:" +
                "\n-d or" + 
                "\n-l,-s or" +
                "\n-n.";

        System.out.println(msg);
        System.exit(exitCode);
    }

    public double getRuntimeFactor() {
        return this.runtimeFactor;
    }

    @Override
    protected void processArgs(String[] args) {
        int c;
        LongOpt[] longopts = new LongOpt[7];

        longopts[0] = new LongOpt("data", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longopts[1] = new LongOpt("expt", LongOpt.REQUIRED_ARGUMENT, null, 'e');
        longopts[2] = new LongOpt("factor", LongOpt.REQUIRED_ARGUMENT, null, 'f');
        longopts[3] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[4] = new LongOpt("lanes", LongOpt.REQUIRED_ARGUMENT, null, 'l');
        longopts[5] = new LongOpt("num-jobs", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longopts[6] = new LongOpt("sequences", LongOpt.REQUIRED_ARGUMENT, null, 's');

        Getopt g = new Getopt("Genome", args, "d:e:f:hl:n:s:", longopts);
        g.setOpterr(false);
        
        double factor = 1.0;
        int numJobs = 0;
        int lanes = 0;
        int sequences = 0;
        long data = 0;

        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'd':
                    data = Long.parseLong(g.getOptarg());
                    break;
                case 'e':
                    this.expt = g.getOptarg();
                    break;
                case 'f':
                    factor = Double.parseDouble(g.getOptarg());
                    this.runtimeFactor = factor;
                    break;
                case 'h':
                    usage(0);
                    break;
                case 'l':
                    lanes = Integer.parseInt(g.getOptarg());
                    break;
                case 'n':
                    numJobs = Integer.parseInt(g.getOptarg());
                    break;
                case 's':
                    sequences = Integer.parseInt(g.getOptarg());
                    break;

                default:
                    usage(1);
            }
        }

        this.referenceSize = Misc.randomLong(4L * 1024 * 1024 * 1024, 0.25);
        
        if (this.expt == null) {
            this.expt = "chr21";
        }
        
        if (data > 0) {
            long singleInputSize = this.distributions.get("sfq_mean").getLong();
            if (data < singleInputSize) {
                throw new RuntimeException("Not enough data: " + data + 
                        "\nMinimum required: " + singleInputSize);
            }
            lanes = (int) Math.ceil(data / this.distributions.get("sfq_mean").getLong());
            
            /*
             * This is completely arbitrary. Try for a better structure later.
             */
            sequences = Misc.randomInt(MAX_SEQUENCES / 10, 0.5);
            numJobs = lanes * 2 + lanes * sequences * 4;
            numJobs = (lanes == 1) ? numJobs + 2 : numJobs + 3;
        }

        if (numJobs > 0) {
            construct(numJobs);
        } else if (lanes > 0 && sequences > 0) {
            this.counts = Misc.closeNonZeroRandoms(lanes, sequences, 0.1);
        } else {
            usage(1);
        }
    }

    private void construct(int numJobs) {

        int bestLanes = 0;
        int bestSplits = 0;
        int leastDiff = Integer.MAX_VALUE;

        for (int lanes = numJobs / laneFactor; lanes > 0; lanes--) {
            int splits = lanes > 1 ? (numJobs - 3 - 2 * lanes) / (4 * lanes) : (numJobs - 4) / 4;
            int remaining = lanes > 1 ? numJobs - 4 * lanes * splits - 2 * lanes - 3 : numJobs - 4 * splits - 4;
            if (remaining >= 0 && remaining < leastDiff) {
                bestSplits = splits;
                bestLanes = lanes;
                leastDiff = remaining;
            }
            /*
             * If we have too many jobs, we don't want a single lane.
             * In that case, break out of loop if we have "reasonable"
             * values.
             */
            if (splits > 4 && remaining <= 3) {
                bestSplits = splits;
                bestLanes = lanes;
                leastDiff = remaining;
                break;
            }

        }

        int[] counts = new int[bestLanes];

        for (int i = 0; i <
                bestLanes; i++) {
            counts[i] = bestSplits;
        }

        if (leastDiff > 4) {
            /*
             * Distribute as many of remaining as possible into additional 
             * splits along some of the lanes. Each split adds 4 new jobs.
             */
            if (bestLanes % 2 == 0) {
                for (int i = 0; leastDiff >=
                        8; i++) {
                    counts[counts.length / 2 - 1 - i]++;
                    counts[counts.length / 2 + i]++;
                    leastDiff -= 8;
                }

            } else {
                for (int i = 0; leastDiff >=
                        8; i++) {
                    counts[counts.length / 2 - 1 - i]++;
                    counts[counts.length / 2 + 1 + i]++;
                    leastDiff -= 8;
                }

                if (leastDiff >= 4) {
                    counts[counts.length / 2]++;
                    leastDiff -= 4;
                }

            }
        }
        this.counts = counts;
    }

    public void construct(int lanes, int splits) {
        int counts[] = new int[lanes];

        for (int i = 0; i <
                counts.length; i++) {
            counts[i] = splits;
        }

        this.counts = counts;
    }

    public void constructWorkflow() {



        List<FastQSplit> fastqSplit = new ArrayList<FastQSplit>();
        for (int i = 0; i <
                counts.length; i++) {
            fastqSplit.add(new FastQSplit(this, "fastqSplit_" + expt, "1.0", getNewJobID(), i));
        }

        List<FilterContams> filterContams = new ArrayList<FilterContams>();
        for (int i = 0; i <
                counts.length; i++) {
            List<FilterContams> subList = new ArrayList<FilterContams>();
            for (int j = 0; j <
                    counts[i]; j++) {
                subList.add(new FilterContams(this, "filterContams_" + expt, "1.0", getNewJobID(), i, j));
            }

            filterContams.addAll(subList);

            fastqSplit.get(i).addChildren(subList);
        }

        List<Sol2Sanger> sol2sanger = new ArrayList<Sol2Sanger>();
        for (int i = 0, sum = 0; i <
                counts.length; i++) {
            for (int j = 0; j <
                    counts[i]; j++) {
                Sol2Sanger s = new Sol2Sanger(this, "sol2sanger_" + expt, "1.0", getNewJobID(), i, j);
                sol2sanger.add(s);
                filterContams.get(sum + j).addChild(s);
            }

            sum += counts[i];
        }

        List<Fast2Bfq> fastq2bfq = new ArrayList<Fast2Bfq>();
        for (int i = 0, sum = 0; i <
                counts.length; i++) {
            for (int j = 0; j <
                    counts[i]; j++) {
                Fast2Bfq s = new Fast2Bfq(this, "fastq2bfq_" + expt, "1.0", getNewJobID(), i, j);
                fastq2bfq.add(s);
                sol2sanger.get(sum + j).addChild(s);
            }

            sum += counts[i];
        }

        List<MaqMap> maqMap = new ArrayList<MaqMap>();
        for (int i = 0, sum = 0; i <
                counts.length; i++) {
            for (int j = 0; j <
                    counts[i]; j++) {
                MaqMap s = new MaqMap(this, "map_" + expt, "1.0", getNewJobID(), i, j);
                maqMap.add(s);
                fastq2bfq.get(sum + j).addChild(s);
            }

            sum += counts[i];
        }

        List<MapMerge> mapMerge1 = new ArrayList<MapMerge>();
        for (int i = 0, sum = 0; i <
                counts.length; i++) {
            MapMerge m = new MapMerge(this, "mapMerge_" + expt, "1.0", getNewJobID(), i);
            mapMerge1.add(m);

            for (int j = 0; j < counts[i]; j++) {
                maqMap.get(sum + j).addChild(m);
            }

            sum += counts[i];
        }

        MapMerge finalMapMerge = null;

        if (mapMerge1.size() > 1) {
            MapMerge mapMerge2 = new MapMerge(this, "mapMerge_" + expt, "1.0", getNewJobID(), 0);

            for (AppJob parent : mapMerge1) {
                parent.addChild(mapMerge2);
            }

            finalMapMerge = mapMerge2;
        } else {
            finalMapMerge = mapMerge1.get(0);
        }

        MaqIndex maqIndex = new MaqIndex(this, "maqindex_" + expt, "1.0", getNewJobID());
        finalMapMerge.addChild(maqIndex);

        PileUp pileup = new PileUp(this, "pileup_" + expt, "1.0", getNewJobID());
        maqIndex.addChild(pileup);
        pileup.finish();
    }

    @Override
    protected void populateDistributions() {
        this.distributions.put("sfq", Distribution.getTruncatedNormalDistribution(351024865.91, 21874201834298820.00));
        this.distributions.put("sfq_mean", Distribution.getConstantDistribution(351024865.91));
        this.distributions.put("fastQSplit_rate",
                Distribution.getTruncatedNormalDistribution(7663927.38, 5641093085489.23));
        this.distributions.put("filterContams_rate", Distribution.getTruncatedNormalDistribution(8474776.63, 25021537629225.05));
        this.distributions.put("sol2sanger_factor", Distribution.getTruncatedNormalDistribution(1.32, 0.1));
        this.distributions.put("sol2sanger_rate", Distribution.getTruncatedNormalDistribution(15073990.10, 12645616180851.43));
        this.distributions.put("fast2bfq_factor", Distribution.getTruncatedNormalDistribution(4.24, 0.16));
        this.distributions.put("fast2bfq_rate", Distribution.getTruncatedNormalDistribution(6797390.86, 1232900899482.26));
        this.distributions.put("maqmap_factor", Distribution.getTruncatedNormalDistribution(0.92, 0.00));
        this.distributions.put("maqmap_rate", Distribution.getTruncatedNormalDistribution(369551.55, 13049619575.46));
        this.distributions.put("mapMerge_factor", Distribution.getConstantDistribution(1.06));
        this.distributions.put("mapMerge_rate", Distribution.getTruncatedNormalDistribution(2267233.18, 108246136965.12));
        this.distributions.put("maqIndex_factor", Distribution.getTruncatedNormalDistribution(64.25, 645.17));
        this.distributions.put("maqIndex_rate", Distribution.getTruncatedNormalDistribution(17679204.35, 22320561695167.98));
        this.distributions.put("pileup_factor", Distribution.getUniformDistribution(0.0, 10.0));
        this.distributions.put("pileup_rate", Distribution.getTruncatedNormalDistribution(881356.45, 8956444331.45));
    }
}

class FastQSplit extends AppJob {

    private final int laneID;
    private final long size;

    public FastQSplit(Genome genome, String name, String version, String jobID, int laneID) {
        super(genome, Genome.namespace, name, version, jobID);
        this.laneID = laneID;
        size = genome.generateLong("sfq");
        input(genome.getExpt() + laneID + ".sfq", size);

        /*
         * Assume runtime scales linearly, although it doesn't seem to be the
         * case. However, the scaling factor for a given w/f is reasonably
         * constant.
         */
        double rate = genome.generateLong("fastQSplit_rate");
        double runtime = size / rate;
        addAnnotation("runtime", String.format("%.2f",
                runtime * genome.getRuntimeFactor()));
    }

    @Override
    protected void addChildren(List<? extends AppJob> children) {
        long[] sizes = Misc.closeNonZeroRandoms(children.size(), this.size, 0.1);
        /*
         * Each fastqSplit generates a unique file for each child.
         */
        int i = 0;
        for (AppJob child : children) {
            addLink(child, ((Genome) getApp()).getExpt() + "." + this.laneID + "." + i + ".sfq", sizes[i]);
            i++;
        }
    }
}

class FilterContams extends AppJob {
    
    public FilterContams(Genome genome, String name, String version, String jobID, int laneID, int splitID) {
        super(genome, Genome.namespace, name, version, jobID);
    }

    @Override
    void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        AppFilename in = null;
        for (AppFilename input : inputs) {
            if (input.getFilename().endsWith(".sfq")) {
                in = input;
                break;
            }
        }
        long[] sizes = Misc.closeNonZeroRandoms(2, in.getSize(), 0.1);
        addLink(child, in.getFilename().replace(".sfq", ".nocontam.sfq"), sizes[0]);
        output(in.getFilename().replace(".sfq", ".contam.sfq"), sizes[1]);

        double runtime = in.getSize() / ((Genome) getApp()).generateDouble("filterContams_rate");
        addAnnotation("runtime", String.format("%.2f", runtime * ((Genome) getApp()).getRuntimeFactor()));
    }
}

class Sol2Sanger extends AppJob {
    
    public Sol2Sanger(Genome genome, String name, String version, String jobID, int laneID, int splitID) {
        super(genome, Genome.namespace, name, version, jobID);
    }

    @Override
    void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        AppFilename in = null;
        for (AppFilename input : inputs) {
            if (input.getFilename().endsWith(".sfq")) {
                in = input;
                break;
            }
        }
        long size = (long) (in.getSize() / ((Genome) getApp()).generateDouble("sol2sanger_factor"));
        addLink(child, in.getFilename().replace(".sfq", ".fq"), size);

        double runtime = in.getSize() / ((Genome) getApp()).generateDouble("sol2sanger_rate");
        addAnnotation("runtime", String.format("%.2f", runtime * ((Genome) getApp()).getRuntimeFactor()));
    }
}

class Fast2Bfq extends AppJob {
    
    public Fast2Bfq(Genome genome, String name, String version, String jobID, int laneID, int splitID) {
        super(genome, Genome.namespace, name, version, jobID);
    }

    @Override
    void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        AppFilename in = null;
        for (AppFilename input : inputs) {
            if (input.getFilename().endsWith(".fq")) {
                in = input;
                break;
            }
        }
        double factor = ((Genome) getApp()).generateDouble("fast2bfq_factor");
        long size = (long) (in.getSize() / factor);
        addLink(child, in.getFilename().replace(".fq", ".bfq"), size);

        double runtime = in.getSize() / ((Genome) getApp()).generateDouble("fast2bfq_rate");
        addAnnotation("runtime", String.format("%.2f", runtime * ((Genome) getApp()).getRuntimeFactor()));
    }
}

class MaqMap extends AppJob {

    public MaqMap(Genome genome, String name, String version, String jobID, int laneID, int splitID) {
        super(genome, Genome.namespace, name, version, jobID);
        input(genome.getExpt() + ".BS.bfa", genome.getReferenceSize());
    }

    @Override
    void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        AppFilename in = null;
        for (AppFilename input : inputs) {
            if (input.getFilename().endsWith(".bfq")) {
                in = input;
                break;
            }
        }
        double factor = ((Genome) getApp()).generateDouble("maqmap_factor");
        long size = (long) (in.getSize() / factor);
        addLink(child, in.getFilename().replace(".bfq", ".map"), size);

        /*
         * Haven't found a good correlation between size of .bfq and runtime.
         * However, runtime does depend on the size of the reference genome.
         * Use a simple model here.
         */
        double runtime = ((Genome) getApp()).getReferenceSize() / ((Genome) getApp()).generateDouble("maqmap_rate");

        addAnnotation("runtime", String.format("%.2f", runtime * ((Genome) getApp()).getRuntimeFactor()));
    }
}

class MapMerge extends AppJob {

    private final int mapID;

    public MapMerge(Genome genome, String name, String version, String jobID, int mapID) {
        super(genome, Genome.namespace, name, version, jobID);
        this.mapID = mapID;
    }

    @Override
    void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        long totalSize = 0;
        for (AppFilename input : inputs) {
            totalSize += input.getSize();
        }

        long size = (long) (totalSize / ((Genome) getApp()).generateDouble("mapMerge_factor"));
        String prefix = ((Genome) getApp()).getExpt();
        if (child instanceof MapMerge) {
            addLink(child, prefix + "_" + this.mapID + ".nocontam.map", size);
        } else {
            addLink(child, prefix + ".nocontam.map", size);
            output(prefix + ".nocontam.map.zr", Misc.randomLong((long) (size * 0.05), 0.25));
            output(prefix + ".nocontam.map.vm", Misc.randomLong((long) (size * 0.005), 0.25));
        }

        double runtime = totalSize / ((Genome) getApp()).generateDouble("mapMerge_rate");
        addAnnotation("runtime", String.format("%.2f", runtime * ((Genome) getApp()).getRuntimeFactor()));
    }
}

class MaqIndex extends AppJob {

    public MaqIndex(Genome genome, String name, String version, String jobID) {
        super(genome, Genome.namespace, name, version, jobID);
    }

    @Override
    void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        AppFilename in = null;
        for (AppFilename input : inputs) {
            if (input.getFilename().endsWith(".map")) {
                in = input;
                break;
            }
        }
        double factor = ((Genome) getApp()).generateDouble("maqIndex_factor");
        long size = (long) (in.getSize() / factor);
        addLink(child, ((Genome) getApp()).getExpt() + ".nocontam.chr.map", size);

        double runtime = size / ((Genome) getApp()).generateDouble("maqIndex_rate");
        addAnnotation("runtime", String.format("%.2f", runtime * ((Genome) getApp()).getRuntimeFactor()));
    }
}

class PileUp extends AppJob {

    public PileUp(Genome genome, String name, String version, String jobID) {
        super(genome, Genome.namespace, name, version, jobID);
        input(genome.getExpt() + ".BSnull.bfa", genome.getReferenceSize());
    }

    public void finish() {
        Set<AppFilename> inputs = getInputs();
        AppFilename in = null;
        for (AppFilename input : inputs) {
            if (input.getFilename().endsWith(".map")) {
                in = input;
                break;
            }
        }
        double factor = ((Genome) getApp()).generateDouble("pileup_factor");
        long size = (long) (in.getSize() * factor);

        output(in.getFilename().replace(".map", ".pileup"), size);
        /*
         * Once again, pileup runtime is calculated solely on the size of the
         * reference genome.
         */

        double runtime = ((Genome) getApp()).getReferenceSize() / ((Genome) getApp()).generateDouble("pileup_rate");
        addAnnotation("runtime", String.format("%.2f", runtime * ((Genome) getApp()).getRuntimeFactor()));
    }
}
