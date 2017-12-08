package simulation.generator.app;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.griphyn.vdl.dax.PseudoText;
import simulation.generator.util.Distribution;
import simulation.generator.util.MemoryModel;
import simulation.generator.util.Misc;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Shishir Bharathi
 */
public class CyberShake extends AbstractApplication {

    private static final int MAX_RUPTURES = 30;
    private static final int MAX_VARIATIONS = 30;
    private static final double BIAS = 1.0 / 20;
    private static final int MIN_INPUTS = 1;
    private static final double EXTRACT_SGT_FACTOR = 0.0081;
//    public static final double DEFAULT_FACTOR = SeismogramSynthesis.MEAN_RUNTIME;
    private double runtimeFactor = 1;

    public enum SITE {
        CCP, DLA, FFI, LADT, LBP, PAS, SABD, SBSM, SMCA, USC, WNGC
    };
    
    public static final String NAMESPACE = "CyberShake";
    private SITE site;
    private int[] counts;
    private int numExtractSGT;

    private void usage(int exitCode) {
        String msg = "CyberShake [-h] [options]." +
                "\n--data | -d Approximate size of input dataset." +
                "\n--factor | -f Avg. runtime to execute an seismogram_synthesis job." +
                "\n--help | -h Print help message." +
                "\n--numjobs | -n Number of jobs." +
                "\n--ruptures | -r Number of ruptures." +
                "\n--site | -s Generate workflow for specified site." +
                "\n--variations | -m Maximum number of variations for any rupture." +
                "\n\nOne of the following combinations is required:" +
                "\n-d or" +
                "\n-r,-v or" +
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
        longopts[1] = new LongOpt("factor", LongOpt.REQUIRED_ARGUMENT, null, 'f');
        longopts[2] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');

        longopts[3] = new LongOpt("num-jobs", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longopts[4] = new LongOpt("ruptures", LongOpt.REQUIRED_ARGUMENT, null, 'r');
        longopts[5] = new LongOpt("site", LongOpt.REQUIRED_ARGUMENT, null, 's');
        longopts[6] = new LongOpt("variations", LongOpt.REQUIRED_ARGUMENT,
                null, 'v');

        Getopt g = new Getopt("CyberShake", args, "d:f:hn:r:s:v:", longopts);
        g.setOpterr(false);

        int numJobs = 0;
        int variations = MAX_VARIATIONS;
        int ruptures = MAX_RUPTURES;
        SITE site = null;
        long data = 0;

        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'd':
                    data = Long.parseLong(g.getOptarg());
                    break;
                case 'f':
                    this.runtimeFactor = Double.parseDouble(g.getOptarg());
                    break;
                case 'h':
                    usage(0);
                    break;
                case 'm':
                    variations = Integer.parseInt(g.getOptarg());
                    break;
                case 'n':
                    numJobs = Integer.parseInt(g.getOptarg());
                    break;
                case 'r':
                    ruptures = Integer.parseInt(g.getOptarg());
                    break;
                case 's':
                    site = SITE.valueOf(g.getOptarg());
                    break;

                default:
                    usage(1);
            }
        }

        if (site == null) {
            site = SITE.FFI;
        }


        int[] counts = null;
        int numExtractSGT = 0;
        int numSeismogramSynthesis = 0;

        if (data > 0) {
            /*
             * Reverse engineer everything from data size.
             */
            long singleInputSize = this.distributions.get("SGT_MEAN").getLong();
            if (data < singleInputSize * MIN_INPUTS) {
                throw new RuntimeException("Not enough data: " + data +
                        "\nMinimum required: " + singleInputSize * MIN_INPUTS);
            }
            numExtractSGT = (int) Math.ceil(data / this.distributions.get("SGT_MEAN").getLong());
            numJobs = numExtractSGT + Misc.randomInt(numExtractSGT * 5, 0.25) + 2;
            numSeismogramSynthesis = (numJobs - 2 - numExtractSGT) / 2;
            counts = Misc.closeNonZeroRandoms(numExtractSGT, numSeismogramSynthesis, 0.25);
            Arrays.sort(counts);
            
        } else if (numJobs > 0) {
            int remaining = numJobs - 2;
            if (remaining < 3) {
                throw new RuntimeException("Cannot generate workflow with numJobs=" + numJobs);
            }

            numExtractSGT = Misc.randomInt((int) (remaining * EXTRACT_SGT_FACTOR), 0.5);
            if (numExtractSGT < 2) {
                numExtractSGT = 2;
            }
            if ((remaining - numExtractSGT) % 2 != 0) {
                if (numExtractSGT > 1) {
                    numExtractSGT--;
                } else {
                    numExtractSGT++;
                }
            }
            numSeismogramSynthesis = (remaining - numExtractSGT) / 2;

            counts = Misc.closeNonZeroRandoms(numExtractSGT, numSeismogramSynthesis, 0.25);
            Arrays.sort(counts);

        } else if (ruptures > 0 && variations > 0) {
            int total = Misc.randomInt(ruptures * variations * 6, 0.1);
            counts = Misc.closeNonZeroRandoms(ruptures * variations, total, 0.25);
            numExtractSGT = ruptures * variations;
        } else {
            usage(1);
        }
        this.site = site;
        this.numExtractSGT = numExtractSGT;
        this.counts = counts;
    }

    public void constructWorkflow() {
        int rupture = 0, variation = 0;

        ZipPSA zipPSA = new ZipPSA(this, "ZipPSA", "1.0", getNewJobID());
        ZipSeis zipSeis = new ZipSeis(this, "ZipSeis", "1.0", getNewJobID());
        for (int i = 0; i < numExtractSGT; i++) {
            if (Misc.randomToss(BIAS)) {
                rupture++;
                variation = 0;
            } else {
                variation++;
            }

            String prefix = site + "_" + rupture + "_" + variation;
            ExtractSGT e = new ExtractSGT(this, "ExtractSGT", "1.0", getNewJobID(), prefix);
            for (int j = 0; j < counts[i]; j++) {
                SeismogramSynthesis s = new SeismogramSynthesis(this, "SeismogramSynthesis", "1.0", getNewJobID(), prefix);
                e.addChild(s);
                s.addChild(zipSeis);

                PeakValCalcOkaya p = new PeakValCalcOkaya(this, "PeakValCalcOkaya", "1.0", getNewJobID());
                s.addChild(p);
                p.addChild(zipPSA);
            }
            e.finish();
        }

        zipPSA.finish();
        zipSeis.finish();

    }


    @Override
    protected void populateDistributions() {
        /*
         * File size distributions.
         */
        distributions.put("SGT", Distribution.getTruncatedNormalDistribution(19958666972.0, 93654683371233792.0));
        distributions.put("SGT_MEAN", Distribution.getConstantDistribution(19958666972.0));
        distributions.put("SUB_SGT", Distribution.getTruncatedNormalDistribution(231720131.58, 27081652820787388.00));
        distributions.put("SLIP", Distribution.getUniformDistribution(0, 10000));
        distributions.put("HIPO", Distribution.getUniformDistribution(0, 10000));
        distributions.put("VARIATION", Distribution.getTruncatedNormalDistribution(3708598.53, 3187576.*3187576.));
        distributions.put("GRM", Distribution.getConstantDistribution(24000));
        distributions.put("BSA", Distribution.getConstantDistribution(216));
        distributions.put("ZipSeis_factor", Distribution.getConstantDistribution(6));
        distributions.put("ZipPSA_factor", Distribution.getConstantDistribution(6));

        /*
         * Runtime distributions.
         */
        distributions.put("ExtractSGT", Distribution.getTruncatedNormalDistribution(137.45, 206d*206));
        distributions.put("SeismogramSynthesis", Distribution.getTruncatedNormalDistribution(43.40, 31d*31));
        distributions.put("PeakValCalcOkaya", Distribution.getTruncatedNormalDistribution(1.09, 3.71));
        distributions.put("ZipSeis_rate", Distribution.getConstantDistribution(228180d));
        distributions.put("ZipPSA_rate", Distribution.getConstantDistribution(2782d));

        /*
         * Memory models.
         */
        memoryModels.put("ExtractSGT", MemoryModel.constant(20.64e6, 0.64e6));
        // assume 90% of the variance in memory consumption is explained by input size
        // TODO revert this to a realistic model, slope 1.49
        memoryModels.put("SeismogramSynthesis", new MemoryModel(10., 2500e6, 483e6));
        memoryModels.put("ZipSeis", MemoryModel.constant(6.25e6, 0.16e6));
        memoryModels.put("PeakValCalcOkaya", MemoryModel.constant(3.11e6,  0.01e6));
        memoryModels.put("ZipPSA", MemoryModel.constant(6.16e6,  0.16e6));

        /*
         * Peak memory relative time distributions.
         */
        Distribution peakMemRelativeTime = Distribution.getUniformDistribution(0.4,0.6);
        distributions.put("ExtractSGT_peak_mem_relative_time", peakMemRelativeTime);
        distributions.put("SeismogramSynthesis_peak_mem_relative_time", peakMemRelativeTime);
        distributions.put("PeakValCalcOkaya_peak_mem_relative_time", peakMemRelativeTime);
        distributions.put("ZipSeis_peak_mem_relative_time", peakMemRelativeTime);
        distributions.put("ZipPSA_peak_mem_relative_time", peakMemRelativeTime);

    }
}

class ExtractSGT extends AppJob {

    private final String prefix;
    private SeismogramSynthesis lastChild;

    public ExtractSGT(CyberShake cybershake, String name, String version, String jobID, String prefix) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);
        this.prefix = prefix;

        long size = cybershake.generateLong("SGT");
        input(prefix + "_fx.sgt", size);
        input(prefix + "_fy.sgt", size);

        double runtime = cybershake.generateDouble("ExtractSGT") * cybershake.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));

        long peakMemory = cybershake.memoryModels.get("ExtractSGT").getPeakMemoryConsumption(2*size);
        double peakMemoryTimeRelative = cybershake.generateDouble("ExtractSGT_peak_mem_relative_time");
        addAnnotation("input_total_bytes", ""+2*size);
        addAnnotation("peak_mem_bytes", ""+peakMemory);
        addArgument(new PseudoText(String.format("peak_mem_bytes=%d,peak_memory_relative_time=%.3f", peakMemory, peakMemoryTimeRelative)));
    }

    public void addChild(AppJob child) {
        long subSize = ((CyberShake) getApp()).generateLong("SUB_SGT");
        addLink(child, prefix + "_subfx.sgt", subSize);
        addLink(child, prefix + "_subfy.sgt", subSize);
        lastChild = (SeismogramSynthesis) child;
    }

    @Override
    public void finish() {
        Set<AppFilename> inputs = lastChild.getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().contains("variation")) {
                input(input);
            }
        }
    }
}


class SeismogramSynthesis extends AppJob {

    private final String prefix;
    private final String jobID;

    public SeismogramSynthesis(CyberShake cybershake, String name, String version, String jobID, String prefix) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);
        this.prefix = prefix;
        this.jobID = jobID;

        int slip = cybershake.generateInt("SLIP");
        int hipo = cybershake.generateInt("HIPO");
        String inputVariation = prefix + "_txt.variation-s" +
                String.format("%05d", slip) + "-h" + String.format("%05d", hipo);

        long size = cybershake.generateLong("VARIATION");
        input(inputVariation, size);

        double runtime = cybershake.generateDouble("SeismogramSynthesis") * cybershake.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));

        long peakMemory = cybershake.memoryModels.get("SeismogramSynthesis").getPeakMemoryConsumption(size);
        double peakMemoryTimeRelative = cybershake.generateDouble("SeismogramSynthesis_peak_mem_relative_time");
        addAnnotation("input_total_bytes", ""+size);
        addAnnotation("peak_mem_bytes", ""+peakMemory);
        addArgument(new PseudoText(String.format("peak_mem_bytes=%d,peak_memory_relative_time=%.3f", peakMemory, peakMemoryTimeRelative)));

    }

    @Override
    public void addChild(AppJob child) {
        addLink(child, "Seismogram_" + prefix + "_" + jobID + ".grm", ((CyberShake) getApp()).generateLong("GRM"));
    }
}
class PeakValCalcOkaya extends AppJob {

    public PeakValCalcOkaya(CyberShake cybershake, String name, String version, String jobID) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);

        double runtime = cybershake.generateDouble("PeakValCalcOkaya") * cybershake.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime * cybershake.getRuntimeFactor()));

        long peakMemory = cybershake.memoryModels.get("PeakValCalcOkaya").getPeakMemoryConsumption((long) (runtime*1e6));
        double peakMemoryTimeRelative = cybershake.generateDouble("PeakValCalcOkaya_peak_mem_relative_time");
        addAnnotation("input_total_bytes", "0");
        addAnnotation("peak_mem_bytes", ""+peakMemory);
        addArgument(new PseudoText(String.format("peak_mem_bytes=%d,peak_memory_relative_time=%.3f", peakMemory, peakMemoryTimeRelative)));
    }

    @Override
    public void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().startsWith("Seismogram")) {
                String temp = input.getFilename();
                temp = temp.replace("Seismogram", "PeakVals");
                temp = temp.replace("grm", "bsa");
                addLink(child, temp, ((CyberShake) getApp()).generateLong("BSA"));
                break;
            }
        }
    }
}

class ZipSeis extends AppJob {

    CyberShake cybershake;
    public ZipSeis(CyberShake cybershake, String name, String version, String jobID) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);
        this.cybershake = cybershake;

    }

    @Override
    public void finish() {
        /*
         * Hack.
         */
        Set<AppFilename> inputs = getInputs();
        long zipSize = Misc.randomLong((long) (inputs.size() * ((CyberShake) getApp()).generateLong("GRM") / ((CyberShake) getApp()).generateDouble("ZipSeis_factor")), 0.25);

        output("Cybershake_Seismograms.zip", zipSize);
        double runtime = zipSize * ((CyberShake) getApp()).getRuntimeFactor() / ((CyberShake) getApp()).generateDouble("ZipSeis_rate");
        addAnnotation("runtime", String.format("%.2f", runtime));

        long peakMemory = cybershake.memoryModels.get("ZipSeis").getPeakMemoryConsumption(zipSize);
        double peakMemoryTimeRelative = cybershake.generateDouble("ZipSeis_peak_mem_relative_time");
        addAnnotation("input_total_bytes", zipSize+"");
        addAnnotation("peak_mem_bytes", ""+peakMemory);
        addArgument(new PseudoText(String.format("peak_mem_bytes=%d,peak_memory_relative_time=%.3f", peakMemory, peakMemoryTimeRelative)));
    }
}

class ZipPSA extends AppJob {

    CyberShake cybershake;

    public ZipPSA(CyberShake cybershake, String name, String version, String jobID) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);
        this.cybershake = cybershake;
    }

    public void finish() {
        /*
         * Hack.
         */
        Set<AppFilename> inputs = getInputs();
        long zipSize = Misc.randomLong((long) (inputs.size() * ((CyberShake) getApp()).generateLong("BSA") / ((CyberShake) getApp()).generateDouble("ZipSeis_factor")), 0.25);

        output("Cybershake_PSA.zip", zipSize);

        double runtime = zipSize * ((CyberShake) getApp()).getRuntimeFactor() / ((CyberShake) getApp()).generateDouble("ZipPSA_rate");
        addAnnotation("runtime", String.format("%.2f", runtime));

        long peakMemory = cybershake.memoryModels.get("ZipPSA").getPeakMemoryConsumption(zipSize);
        double peakMemoryTimeRelative = cybershake.generateDouble("ZipPSA_peak_mem_relative_time");
        addAnnotation("input_total_bytes", zipSize+"");
        addAnnotation("peak_mem_bytes", ""+peakMemory);
        addArgument(new PseudoText(String.format("peak_mem_bytes=%d,peak_memory_relative_time=%.3f", peakMemory, peakMemoryTimeRelative)));
    }
}
