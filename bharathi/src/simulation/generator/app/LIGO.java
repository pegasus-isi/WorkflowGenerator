package simulation.generator.app;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import simulation.generator.util.Misc;
import java.util.Set;

import java.util.Arrays;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import org.griphyn.vdl.classes.LFN;
import simulation.generator.util.Distribution;

/**
 * @author Shishir Bharathi
 */
public class LIGO extends AbstractApplication {

    public static final String namespace = "LIGO";
    private static final double OVERLAP_FACTOR = 1.05;
    private static final int MAX_TRIES = 100;
    private double runtimeFactor = 1;
    private int[] topDown;
    private int bnCount;
    private int totalEdges;

    protected String getNamespace() {
        return namespace;
    }

    private void usage(int exitCode) {
        String msg = "LIGO [-h] [options]." +
                "\n--data | -d Approximate size of input data." +
                "\n--factor | -f Avg. runtime to execute an TmpltBank job." +
                "\n--help | -h Print help message." +
                "\n--numjobs | -n Number of jobs.";

        System.out.println(msg);
        System.exit(exitCode);
    }

    public double getRuntimeFactor() {
        return this.runtimeFactor;
    }

    @Override
    protected void processArgs(String[] args) {
        int c;
        LongOpt[] longopts = new LongOpt[4];

        longopts[0] = new LongOpt("data", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longopts[1] = new LongOpt("factor", LongOpt.REQUIRED_ARGUMENT, null, 'f');
        longopts[2] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[3] = new LongOpt("numjobs", LongOpt.REQUIRED_ARGUMENT, null, 'n');

        Getopt g = new Getopt("Genome", args, "d:f:hn:", longopts);
        g.setOpterr(false);
        
        double factor = 1.0;
        int numJobs = 0;
        long data = 0;

        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'd':
                    data = Long.parseLong(g.getOptarg());
                    break;
                case 'f':
                    factor = Double.parseDouble(g.getOptarg());
                    this.runtimeFactor = factor / generateDouble("TmpltBank_mean");
                    break;
                case 'h':
                    usage(0);
                    break;
                case 'n':
                    numJobs = Integer.parseInt(g.getOptarg());
                    break;
                default:
                    usage(1);
            }
        }

        if (data > 0) {
            topDown = new int[2];
            
            long singleInputSize = this.distributions.get("GWF_MEAN").getLong();
            if (data < singleInputSize) {
                throw new RuntimeException("Not enough data: " + data
                        + "\nMinimum required: " + singleInputSize);
            }
            
            topDown[0] = (int) Math.ceil(data / this.distributions.get("GWF_MEAN").getLong());
            topDown[1] = Misc.randomInt(topDown[0], (int) (topDown[0] * 1.1));
            
            totalEdges = Misc.randomInt(topDown[1], (int) Math.floor(topDown[1] * OVERLAP_FACTOR));
            bnCount = Misc.randomInt(totalEdges / topDown[0], totalEdges * 2 / topDown[0]);
            
        } else if (numJobs > 0) {
            /*
             * First, choose a "small" number of bottleneck jobs.
             */
            if (numJobs <= 20) {
                throw new RuntimeException("Too few jobs.");
            }

            if (numJobs % 2 != 0) {
                throw new RuntimeException("Number of jobs must be even: " + numJobs);
            }

            int max = (int) Math.floor(0.05 * numJobs);
            max = max < 3 ? 3 : max;

            this.bnCount = Misc.randomInt(1, max);
            if (this.bnCount == 1 && (numJobs - 2) % 4 != 0) {
                this.bnCount++;
            }

            int remaining = numJobs - (2 * this.bnCount);

            /*
             * Remaining jobs have to be divided into 2 sets of 2 levels.
             * NOTE: remaining is divisible by 2.
             */

            this.totalEdges = 0;
            for (int i = 0; i < MAX_TRIES; i++) {
                this.topDown = Misc.closeNonZeroRandoms(2, remaining / 2, 0.1);
                Arrays.sort(topDown);
                this.totalEdges = Misc.randomInt(topDown[1],
                        (int) Math.floor(topDown[1] * OVERLAP_FACTOR));

                if (this.totalEdges <= this.bnCount * topDown[0]) {
                    break;
                }
            }

            if (totalEdges > bnCount * topDown[0]) {
                throw new RuntimeException("Could not find a good distribution. (top, down, edges, bn) = " + topDown[0] + ", " + topDown[1] + ", " + totalEdges + ", " + bnCount);
            }

        } else {
            usage(1);
        }
    }

    public void constructWorkflow() {

        List<TmpltBank> tmpltBanks = new ArrayList<TmpltBank>();
        for (int i = 0; i <
                topDown[0]; i++) {
            TmpltBank t = new TmpltBank(this, "TmpltBank", "1.0", getNewJobID());
            tmpltBanks.add(t);
        }

        List<Inspiral> upperInspirals = new ArrayList<Inspiral>();
        for (int i = 0; i <
                topDown[0]; i++) {
            upperInspirals.add(new Inspiral(this, "Inspiral", "1.0", getNewJobID(), 1, i));
        }

        List<Thinca> upperThincas = new ArrayList<Thinca>();
        for (int i = 0; i <
                bnCount; i++) {
            upperThincas.add(new Thinca(this, "Thinca", "1.0", getNewJobID(), 2));
        }

        List<TrigBank> trigBanks = new ArrayList<TrigBank>();
        for (int i = 0; i <
                topDown[1]; i++) {
            trigBanks.add(new TrigBank(this, "TrigBank", "1.0", getNewJobID()));
        }

        List<Inspiral> lowerInspirals = new ArrayList<Inspiral>();
        for (int i = 0; i <
                topDown[1]; i++) {
            lowerInspirals.add(new Inspiral(this, "Inspiral", "1.0", getNewJobID(), 4, i));
        }

        List<Thinca> lowerThincas = new ArrayList<Thinca>();
        for (int i = 0; i <
                bnCount; i++) {
            lowerThincas.add(new Thinca(this, "Thinca", "1.0", getNewJobID(), 5));
        }

        for (int i = 0; i <
                topDown[0]; i++) {
            Set<AppFilename> inputs = new HashSet<AppFilename>();
            inputs.add(new AppFilename(String.format("H-H1_RDS_L4-%d-1024.gwf",
                    TmpltBank.KEY1 + i), LFN.INPUT,
                    generateLong("GWF")));
            inputs.add(new AppFilename(String.format("H-H1_RDS_L4-%d-1024.gwf",
                    TmpltBank.KEY2 + i), LFN.INPUT,
                    generateLong("GWF")));
            inputs.add(new AppFilename(String.format("H-H1_RDS_L4-%d-1024.gwf",
                    TmpltBank.KEY2 + i), LFN.INPUT,
                    generateLong("GWF")));
            inputs.add(new AppFilename(TmpltBank.FAC_DARM, LFN.INPUT, generateLong("FAC_DARM")));
            inputs.add(new AppFilename(TmpltBank.REF_DARM, LFN.INPUT, generateLong("REF_DARM")));

            tmpltBanks.get(i).addInputs(inputs);
            upperInspirals.get(i).addInputs(inputs);
            lowerInspirals.get(i).addInputs(inputs);
        }

        for (int i = topDown[0]; i <
                topDown[1]; i++) {
            Set<AppFilename> inputs = new HashSet<AppFilename>();
            inputs.add(new AppFilename(String.format("H-H2_RDS_L4-%d-1024.gwf",
                    TmpltBank.KEY1 + i), LFN.INPUT,
                    generateLong("GWF")));
            inputs.add(new AppFilename(String.format("H-H2_RDS_L4-%d-1024.gwf",
                    TmpltBank.KEY2 + i), LFN.INPUT,
                    generateLong("GWF")));
            inputs.add(new AppFilename(String.format("H-H2_RDS_L4-%d-1024.gwf",
                    TmpltBank.KEY2 + i), LFN.INPUT,
                    generateLong("GWF")));
            inputs.add(new AppFilename(TmpltBank.FAC_DARM, LFN.INPUT, generateLong("FAC_DARM")));
            inputs.add(new AppFilename(TmpltBank.REF_DARM, LFN.INPUT, generateLong("REF_DARM")));

            lowerInspirals.get(i).addInputs(inputs);
        }

        /*
         * XXX The order of adding children is important.
         * TODO Figure out better way to add children
         */
        for (int i = 0; i < topDown[0]; i++) {
            tmpltBanks.get(i).addChild(upperInspirals.get(i));
        }
        
        int[] bnSet = Misc.maxNonZeroRandomSet(bnCount, totalEdges,
                tmpltBanks.size());
        if (totalEdges < trigBanks.size()) {
            throw new RuntimeException("Count: " + totalEdges);
        } else {
            int sum = 0;
            for (int aBnSet : bnSet) {
                sum += aBnSet;
            }
            
            if (sum != totalEdges) {
                throw new RuntimeException("Count, Sum: " + totalEdges + ", " + sum);
            }

        }
        Arrays.sort(bnSet);
        Misc.reverse(bnSet);

        /*
         * Figure out good starting points for each bottleneck job.
         */
        for (int i = 0,   prev = 0; i < upperThincas.size(); i++) {
            int start = prev;
            if (start + bnSet[i] > upperInspirals.size()) {
                start = upperInspirals.size() - bnSet[i];
            }
            
            for (int j = 0; j < bnSet[i]; j++) {
                upperInspirals.get(start + j).addChild(upperThincas.get(i));
            }
            
            prev = start + bnSet[i];
        }

        /*
         * The counter needs to go all the way to lowerThincas.size().
         */
        for (int i = 0,   prev = 0; i <
                lowerThincas.size(); i++) {
            int start = prev;
            if (start + bnSet[i] > trigBanks.size()) {
                start = trigBanks.size() - bnSet[i];
            }
            /*
             * Since top2.length <= down1.length, we don't need
             * additional correction.
             */

            for (int j = 0; j <
                    bnSet[i]; j++) {
                upperThincas.get(i).addChild(trigBanks.get(start + j));
            }

            prev = start + bnSet[i];
        }
        
        for (int i = 0; i < topDown[1]; i++) {
            trigBanks.get(i).addChild(lowerInspirals.get(i));
        }
        
        for (int i = 0, prev = 0; i < lowerThincas.size(); i++) {
            int start = prev;
            if (start + bnSet[i] > trigBanks.size()) {
                start = trigBanks.size() - bnSet[i];
            }
            /*
             * Since top2.length <= down1.length, we don't need
             * additional correction.
             */
            
            for (int j = 0; j < bnSet[i]; j++) {
                lowerInspirals.get(start + j).addChild(lowerThincas.get(i));
            }
            
            prev = start + bnSet[i];
        }
        
        for (Thinca thinca : lowerThincas) {
            thinca.finish();
        }
    }

    @Override
    protected void populateDistributions() {
        /*
         * File size stuff.
         */
        this.distributions.put("GWF", Distribution.getTruncatedNormalDistribution(15595958.08, 334629862.67));
        this.distributions.put("GWF_MEAN", Distribution.getConstantDistribution(15595958.08));
        this.distributions.put("TMPLTBANK.xml", Distribution.getTruncatedNormalDistribution(986917.26, 359037821.49));
        this.distributions.put("FAC_DARM", Distribution.getConstantDistribution(8415922));
        this.distributions.put("REF_DARM", Distribution.getConstantDistribution(902334));

        this.distributions.put("INSPIRAL.xml", Distribution.getTruncatedNormalDistribution(312935.68, 335782981511.87));
        this.distributions.put("INJECTION.xml", Distribution.getConstantDistribution(2421423));
        this.distributions.put("THINCA.xml", Distribution.getTruncatedNormalDistribution(33820.71, 1888180029.78));
        this.distributions.put("TRIGBANK.xml", Distribution.getTruncatedNormalDistribution(12779.67, 137597136.79));

        /*
         * Runtime stuff.
         */
        this.distributions.put("TmpltBank", Distribution.getTruncatedNormalDistribution(18.14, 0.18));
        this.distributions.put("TmltBank_mean", Distribution.getConstantDistribution(18.14));
        this.distributions.put("Inspiral", Distribution.getTruncatedNormalDistribution(460.21, 297397.45));
        this.distributions.put("Thinca", Distribution.getTruncatedNormalDistribution(5.37, 0.06));
        this.distributions.put("TrigBank", Distribution.getTruncatedNormalDistribution(5.11, 0.1));
    }
}

class TmpltBank extends AppJob {
    
    public static final int KEY1 = Misc.randomInt(800000000, 0.1);
    public static final int KEY2;
    private static final int KEY3;
    
    static {
        int key;
        do {
            key = Misc.randomInt(800000000, 0.1);
        } while (KEY1 == key);
        KEY2 = key;

        do {
            key = Misc.randomInt(800000000, 0.1);
        } while (KEY1 == key || KEY2 == key);
        KEY3 = key;
    }
    public static final String FAC_DARM = String.format("H-CAL_FAC_DARM_ERR_H1_S5_U_060-%d-%d.gwf",
            Misc.randomInt(800000000, 0.1), Misc.randomInt(300000000, 0.1));
    public static final String REF_DARM = String.format("H-CAL_REF_DARM_ERR_H1_S5_V1-%d-8.gwf",
            Misc.randomInt(800000000, 0.1));

    public TmpltBank(LIGO ligo, String name, String version, String jobID) {
        super(ligo, LIGO.namespace, name, version, jobID);
        this.setLevel(0);
        double runtime = ligo.generateDouble("TmpltBank") * ligo.getRuntimeFactor();
        addAnnotation("runtime",
                String.format("%.2f", runtime));
    }

    public void addInputs(Set<AppFilename> inputs) {
        input(inputs);
    }

    @Override
    public void addChild(AppJob child) {
        String name = String.format("H1-TMPLTBANK-%d-2048.xml", Misc.randomInt(800000000, 0.1));
        long size = ((LIGO) getApp()).generateLong("TMPLTBANK.xml");
        addLink(child, name, size);
    }
}

class Inspiral extends AppJob {
    
    private static final int INJECTION_KEY1 = Misc.randomInt(800000000, 0.1);
    private static final int INJECTION_KEY2 = Misc.randomInt(8000000, 0.1);

    public Inspiral(LIGO ligo, String name, String version, String jobID, int level,
            int id) {
        super(ligo, LIGO.namespace, name, version, jobID);
        this.setLevel(level);
        /*
         * All inspirals use the same injections file.
         */
        input(String.format("HL-INJECTIONS_100-%d-%d.xml", INJECTION_KEY1, INJECTION_KEY2),
                ligo.generateInt("INJECTION.xml"));
        double runtime = ligo.generateDouble("Inspiral") * ligo.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));
    }

    public void addInputs(Set<AppFilename> inputs) {
        input(inputs);
    }

    @Override
    public void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().contains("TMPLTBANK")) {
                String filename = input.getFilename().replace("TMPLTBANK", "INSPIRAL");
                long size = ((LIGO) getApp()).generateLong("INSPIRAL.xml");
                addLink(child, filename, size);
            } else if (input.getFilename().contains("TRIGBANK")) {
                String filename = input.getFilename().replace("TRIGBANK", "INSPIRAL");
                long size = ((LIGO) getApp()).generateLong("INSPIRAL.xml");
                addLink(child, filename, size);
            }
        }
    }
}

class Thinca extends AppJob {
    
    public Thinca(LIGO ligo, String name, String version, String jobID, int level) {
        super(ligo, LIGO.namespace, name, version, jobID);
        this.setLevel(level);
        double runtime = ligo.generateDouble("Thinca") * ligo.getRuntimeFactor();
        addAnnotation("runtime",
                String.format("%.2f", runtime));
    }

    private void generateOutput(AppJob child) {
        String filename = null;
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            String name = input.getFilename();
            if (name.contains("INSPIRAL")) {
                if (filename == null) {
                    filename = name.replace("INSPIRAL", "THINCA");
                } else {
                    CharSequence prefix = name.subSequence(0, 2);
                    if (!filename.contains(prefix)) {
                        filename = prefix + filename;
                    }
                }
            }
        }
        long size = ((LIGO) getApp()).generateInt("THINCA.xml");
        if (child != null) {
            addLink(child, filename, size);
        } else {
            output(filename, size);
        }
    }

    @Override
    public void addChild(AppJob child) {
        generateOutput(child);
    }

    public void finish() {
        generateOutput(null);
    }
}

class TrigBank extends AppJob {
    
    public TrigBank(LIGO ligo, String name, String version, String jobID) {
        super(ligo, LIGO.namespace, name, version, jobID);
        this.setLevel(3);
        double runtime = ligo.generateDouble("TrigBank") * ligo.getRuntimeFactor();
        addAnnotation("runtime",
                String.format("%.2f", runtime * ligo.getRuntimeFactor()));
    }

    @Override
    public void addChild(AppJob child) {
        String prefix = null;
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().contains("THINCA")) {
                int index = input.getFilename().indexOf('-');
                prefix = input.getFilename().substring(0, index);
                break;
            }
        }
        String filename = String.format("H1-TRIGBANK_%s-%d-%d.xml", prefix,
                Misc.randomInt(800000000, 0.1), Misc.randomInt(0, 10000));
        long size = ((LIGO) getApp()).generateLong("TRIGBANK.xml");
        addLink(child, filename, size);
    }
}
