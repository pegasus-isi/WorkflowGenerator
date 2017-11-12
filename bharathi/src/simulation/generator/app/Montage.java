package simulation.generator.app;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import org.griphyn.vdl.dax.Leaf;
import org.griphyn.vdl.dax.PseudoText;
import simulation.generator.util.Misc;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.util.MathUtils;
import simulation.generator.util.Distribution;

/**
 * @author Shishir Bharathi
 */
public class Montage extends AbstractApplication {

    public static final String namespace = "Montage";
    private static final int INPUTS_1_DEGREE = 50;
    public static Log logger = LogFactory.getLog(Montage.class);
    private final double factor = 4.0;
    private double degree;
    private double runtimeFactor = 1;
    private int numProj;
    private int numDiff;
    private static final double DEFAULT_PROBABILITY = 0.05;
    private static final int MIN_INPUTS = 5;

    public String getNamespace() {
        return namespace;
    }

    public double getDegree() {
        return this.degree;
    }

    public int getNumProj() {
        return this.numProj;
    }

    public int getNumDiff() {
        return this.numDiff;
    }

    public double getRuntimeFactor() {
        return this.runtimeFactor;
    }

    private void usage(int exitCode) {
        String msg = "Montage [-h] [options]" +
                "\n--data | -d Approximate size of input data." +
                "\n--factor | -f Avg. runtime to execute an mProject job." +
                "\n--help | -h Print help message." +
                "\n--inputs | -i Number of inputs." +
                "\n--numjobs | -n Number of jobs." +
                "\n--overlap-probability | -p Probability any two inputs overlap." +
                "\n--square | -s Square degree of workflow." +
                "\n\nOne of the following combinations is required:" +
                "\n-d or" +
                "\n-s, -p -i or" +
                "\n-n";

        System.out.println(msg);
        System.exit(exitCode);
    }

    @Override
    protected void processArgs(String[] args) {
        int c;
        LongOpt[] longopts = new LongOpt[7];

        longopts[0] = new LongOpt("data", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longopts[1] = new LongOpt("factor", LongOpt.REQUIRED_ARGUMENT, null, 'f');
        longopts[2] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[3] = new LongOpt("num-jobs", LongOpt.REQUIRED_ARGUMENT,
                null, 'n');
        longopts[4] = new LongOpt("inputs", LongOpt.REQUIRED_ARGUMENT, null, 'i');
        longopts[5] = new LongOpt("overlap-probability", LongOpt.REQUIRED_ARGUMENT, null, 'p');
        longopts[6] = new LongOpt("square", LongOpt.REQUIRED_ARGUMENT, null, 's');
        Getopt g = new Getopt("AppGenerator", args, "d:f:hi:n:p:", longopts);
        g.setOpterr(false);
        
        int numJobs = 0;
        int inputs = 0;
        double prob = -1;
        long data = 0;

        while ((c = g.getopt()) != -1) {
            switch (c) {

                case 'd':
                    data = Long.parseLong(g.getOptarg());
                    break;

                case 'f':
                    this.runtimeFactor = Double.parseDouble(g.getOptarg()) / generateDouble("mProjectPP_mean");
                    break;

                case 'h':
                    usage(0);

                    break;

                case 'i':
                    inputs = Integer.parseInt(g.getOptarg());

                    break;

                case 'n':
                    numJobs = Integer.parseInt(g.getOptarg());

                    break;

                case 'p':
                    prob = Double.parseDouble(g.getOptarg());

                    break;

                case 's':
                    this.degree = Double.parseDouble(g.getOptarg());

                    break;
                default:
                    usage(1);
            }
        }

        if (data > 0) {
            long singleInputSize = this.distributions.get("2mass.fits").getLong();
            if (data < singleInputSize * MIN_INPUTS) {
                throw new RuntimeException("Not enough data: " + data +
                        "\nMinimum required: " + singleInputSize * MIN_INPUTS);
            }

            this.numProj = (int) Math.ceil(data / singleInputSize);
            
            if (this.numProj < MIN_INPUTS) {
                throw new RuntimeException("Data results in too few mProjectPP jobs: " + this.numProj);
            }
            
            this.numDiff = (int) Math.round(MathUtils.binomialCoefficient(numProj, 2) * DEFAULT_PROBABILITY);
            this.degree = Math.sqrt((double) this.numProj / INPUTS_1_DEGREE);
        } else {

            if (this.degree > 0 && prob >= 0) {
                if (inputs > 0) {
                    this.numProj = inputs;
                } else {
                    this.numProj = (int) Math.round(INPUTS_1_DEGREE * degree * degree);
                    if (this.numProj < MIN_INPUTS) {
                        this.numProj = MIN_INPUTS;
                    }
                }

                this.numDiff = (int) Math.round(MathUtils.binomialCoefficient(numProj, 2) * prob);
            } else if (numJobs > 0) {
                /*
                 * Looks like:
                 * X mProject jobs
                 * Y mDiffFit jobs, Y >> X
                 * 1 mConcatFit job
                 * 1 mBgModel job
                 * X mBackground jobs
                 * 1 mImgTbl job
                 * 1 mAdd job
                 * 1 mShrink job
                 * 1 mJPEG job
                 */

                int remaining = numJobs - 6;
                if (remaining < 9) {
                    throw new RuntimeException("Not enough jobs.");
                }

                this.numProj = (int) Math.round(remaining / (this.factor + 2));

                while (MathUtils.binomialCoefficient(numProj, 2) < remaining - 2 * this.numProj) {
                    this.numProj++;
                }

                this.numDiff = remaining - 2 * this.numProj;
                this.degree = Math.sqrt((double) this.numProj / INPUTS_1_DEGREE);
            } else {
                usage(1);
            }
        }
    }

    public void constructWorkflow() {

        List<MProjectPP> mProject = new ArrayList<MProjectPP>();
        for (int i = 0; i < numProj; i++) {
            mProject.add(new MProjectPP(this, "mProjectPP", "1.0", getNewJobID()));
        }

        List<MDiffFit> mDiffFit = new ArrayList<MDiffFit>();
        for (int i = 0; i < numDiff; i++) {
            mDiffFit.add(new MDiffFit(this, "mDiffFit", "1.0", getNewJobID()));
        }

        /*
         * Each mDiffFit job gets 2 mProject parents.
         * Howver, no mDiffFit job gets the same 2 parents.
         * TODO: This may not be scalable to very big workflows.
         * However, the scalability of the generator at that level has not
         * been tested yet.
         */
        int[][] connectivity = new int[numProj][numProj];
        for (int count = numDiff; count > 0;) {
            int i = Misc.randomInt(0, numProj);
            int j = Misc.randomInt(0, numProj);
            if (connectivity[i][j] == 0) {
                connectivity[i][j] = 1;
                count--;
            }
        }

        for (int i = 0,  idx = 0; i < numProj; i++) {
            for (int j = 0; j < numProj; j++) {
                if (connectivity[i][j] != 0) {
                    mProject.get(i).addChild(mDiffFit.get(idx));
                    mProject.get(j).addChild(mDiffFit.get(idx));

                    idx++;
                }
            }
        }

        MConcatFit mConcatFit = new MConcatFit(this, "mConcatFit", "1.0", getNewJobID());
        for (MDiffFit m : mDiffFit) {
            m.addChild(mConcatFit);
        }

        MBgModel mBgModel = new MBgModel(this, "mBgModel", "1.0", getNewJobID());
        mConcatFit.addChild(mBgModel);

        List<MBackground> mBackground = new ArrayList<MBackground>();
        for (int i = 0; i < numProj; i++) {
            mBackground.add(new MBackground(this, "mBackground", "1.0", getNewJobID()));
        }

        mBgModel.addChildren(mBackground);
        for (int i = 0; i < mProject.size(); i++) {
            mProject.get(i).addChild(mBackground.get(i));
        }


        MImgTbl mImgTbl = new MImgTbl(this, "mImgTbl", "1.0", getNewJobID());
        for (MBackground aMBackground : mBackground) {
            aMBackground.addChild(mImgTbl);
        }

        MAdd mAdd = new MAdd(this, "mAdd", "1.0", getNewJobID());
        mImgTbl.addChild(mAdd);

        MShrink mShrink = new MShrink(this, "mShrink", "1.0", getNewJobID());
        mAdd.addChild(mShrink);

        MJPEG mJPEG = new MJPEG(this, "mJPEG", "1.0", getNewJobID());
        mShrink.addChild(mJPEG);

        mJPEG.finish();
    }

    @Override
    protected void populateDistributions() {

        /*
         * All values are for a 1.0 degree workflow. These need to be
         * scaled appropriately to generate workflows of other degrees or 
         * based on the number of inputs processed.
         */

        this.distributions.put("region.hdr", Distribution.getConstantDistribution(304));
        this.distributions.put("2mass.fits", Distribution.getConstantDistribution(4222080));
        this.distributions.put("p2mass.fits", Distribution.getTruncatedNormalDistribution(4162432.00, 113770496.00));
        this.distributions.put("fit.txt", Distribution.getTruncatedNormalDistribution(273.74, 98.96));
        this.distributions.put("diff.fits", Distribution.getTruncatedNormalDistribution(284097.20, 21782993947.28));
        this.distributions.put("fits.tbl_base", Distribution.getConstantDistribution(209.953));
        this.distributions.put("fits_list.tbl_base", Distribution.getConstantDistribution(27.2617));
        this.distributions.put("pimages.tbl_base", Distribution.getConstantDistribution(167.556));
        this.distributions.put("cimages.tbl_base", Distribution.getConstantDistribution(167.556));
        this.distributions.put("corrections.tbl_base", Distribution.getConstantDistribution(53.1778));
        this.distributions.put("newcimages.tbl_base", Distribution.getTruncatedNormalDistribution(352.24, 2309.84));
        this.distributions.put("mosaic.fits", Distribution.getConstantDistribution(173465280));

        this.distributions.put("mProjectPP", Distribution.getTruncatedNormalDistribution(13.59, 0.06));
        this.distributions.put("mProjectPP_mean", Distribution.getConstantDistribution(13.59));
        this.distributions.put("mDiffFit", Distribution.getTruncatedNormalDistribution(10.59, 0.01));
        this.distributions.put("mConcatFit_base", Distribution.getTruncatedNormalDistribution(0.08, 0.00));
        this.distributions.put("mBgModel_base", Distribution.getTruncatedNormalDistribution(0.13, 0.01));
        this.distributions.put("mBackground", Distribution.getTruncatedNormalDistribution(10.74, 0.03));
        this.distributions.put("mImgTbl_base", Distribution.getTruncatedNormalDistribution(0.37, 0.01));
        this.distributions.put("mShrink_factor", Distribution.getConstantDistribution(24.99));

        this.distributions.put("mAdd", Distribution.getTruncatedNormalDistribution(30.11, 0.05));
        this.distributions.put("mShrink", Distribution.getTruncatedNormalDistribution(12.21, 0.00));

        this.distributions.put("mJPEG_rate", Distribution.getTruncatedNormalDistribution(549291.00, 3933630100.67));
    }
}

class MProjectPP extends AppJob {
    private final String filename;
    
    public MProjectPP(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);
        
        input("region.hdr", montage.generateLong("region.hdr"));
        this.filename = "2mass-atlas-" + jobID + "s-j" + jobID;
        // TODO: input filesize may vary with degree in some workflows.
        input(this.filename + ".fits", montage.generateLong("2mass.fits"));

        double runtime = montage.generateDouble("mProjectPP");
        addAnnotation("runtime",
                String.format("%.2f", runtime * montage.getRuntimeFactor()));
    }

    @Override
    public void addChild(AppJob child) {
        long size = ((Montage) getApp()).generateLong("p2mass.fits");
        addLink(child, "p" + filename + ".fits", size);
        addLink(child, "p" + filename + "_area.fits", size);
    }
}

class MDiffFit extends AppJob {

    public MDiffFit(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);
        
        input("region.hdr", montage.generateLong("region.hdr"));
        double runtime = montage.generateDouble("mDiffFit");
        addAnnotation("runtime",
                String.format("%.2f", runtime * montage.getRuntimeFactor()));
    }

    @Override
    public void addChild(AppJob child) {
        addLink(child, "fit" + getID() + ".txt", ((Montage) getApp()).generateLong("fit.txt"));
        addLink(child, "diff" + getID() + ".txt", ((Montage) getApp()).generateLong("diff.fits"));
    }
}

class MConcatFit extends AppJob {

    public static final double BASE = 0.3;

    public MConcatFit(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);

        long fitsListSize = (long) (montage.generateDouble("fits_list.tbl_base") * montage.getNumDiff());
        input("fits_list.tbl", fitsListSize);
        double runtime = montage.generateDouble("mConcatFit_base") * montage.getNumDiff() * montage.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));
    }

    @Override
    public void addChild(AppJob child) {
        long fitsTblSize = (long) (((Montage) getApp()).generateDouble("fits.tbl_base") * ((Montage) getApp()).getNumDiff());
        addLink(child, "fits.tbl", fitsTblSize);
    }
}

class MBgModel extends AppJob {

    public MBgModel(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);

        long pimagesTblSize = (long) (montage.generateDouble("pimages.tbl_base") * montage.getNumProj());
        input("pimages.tbl", pimagesTblSize);

        double runtime = montage.generateDouble("mBgModel_base") * montage.getNumDiff() * montage.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));
    }

    @Override
    public void addChildren(List<? extends AppJob> children) {
        for (AppJob child : children) {
            long correctionsTblSize = (long) (((Montage) getApp()).generateDouble("corrections.tbl_base") * ((Montage) getApp()).getNumProj());
            addLink(child, "corrections.tbl", correctionsTblSize);
        }
    }
}

class MBackground extends AppJob {

    public MBackground(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);
        double runtime = montage.generateDouble("mBackground");
        addAnnotation("runtime",
                String.format("%.2f", runtime * montage.getRuntimeFactor()));
    }

    @Override
    public void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            String filename = input.getFilename();
            if (filename.startsWith("p2mass")) {
                String temp = filename.replaceFirst("p2mass", "c2mass");
                String size = input.getAnnotations().get("size");
                addLink(child, temp, Integer.parseInt(size));
            }
        }
    }
}

class MImgTbl extends AppJob {

    public MImgTbl(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);

        long cimagesTblSize = (long) (montage.generateDouble("cimages.tbl_base") * montage.getNumProj());
        input("cimages.tbl", cimagesTblSize);

        double runtime = montage.generateDouble("mImgTbl_base") * montage.getNumProj() * montage.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));
    }

    @Override
    public void addChild(AppJob child) {
        long newcimagesTblSize = (long) (((Montage) getApp()).generateDouble("newcimages.tbl_base") * ((Montage) getApp()).getNumProj());
        addLink(child, "newcimages.tbl",
                newcimagesTblSize);
    }
}

class MAdd extends AppJob {

    private final String jobID;

    public MAdd(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);

        double runtime = montage.generateDouble("mAdd") * montage.getDegree() * montage.getDegree();
        addAnnotation("runtime", String.format("%.2f", runtime * montage.getRuntimeFactor()));
        long filesize = montage.generateLong("region.hdr");
        
        String filesizeString = ""+filesize;
        addArgument(new PseudoText(filesizeString));
        addAnnotation("peak_mem_bytes", filesizeString);
        input("region.hdr", filesize);
        this.jobID = jobID;

    }

    @Override
    public void addChild(AppJob child) {
        String filename = "mosaic_" + jobID + "_" + jobID;

        long size = Misc.randomLong((long) (((Montage) getApp()).generateDouble("mosaic.fits") * ((Montage) getApp()).getDegree()), 0.1);
        addLink(child, filename + ".fits", size);
        addLink(child, filename + "_area.fits", size);
    }
}

class MShrink extends AppJob {

    private final String jobID;

    public MShrink(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);

        double runtime = montage.generateDouble("mShrink") * montage.getDegree();
        addAnnotation("runtime", String.format("%.2f", runtime * montage.getRuntimeFactor()));
        this.jobID = jobID;
    }

    @Override
    public void addChild(AppJob child) {

        long fitsSize = 0;
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().startsWith("mosaic")) {
                fitsSize = input.getSize();
                break;
            }
        }
        long shrunkenFitsSize = (long) (fitsSize / ((Montage) getApp()).generateDouble("mShrink_factor"));

        addLink(child, "shrunken_" + jobID + "_" + jobID + ".fits", shrunkenFitsSize);
    }
}

class MJPEG extends AppJob {

    public MJPEG(Montage montage, String name, String version, String jobID) {
        super(montage, Montage.namespace, name, version, jobID);
    }

    @Override
    public void finish() {
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().startsWith("shrunken")) {
                String temp = input.getFilename().replace(".fits", ".jpg");
                long size = input.getSize();

                long jpgSize = Misc.randomLong(size / 4, .25);
                output(temp, jpgSize);

                double runtime = jpgSize / ((Montage) getApp()).generateDouble("mJPEG_rate");
                addAnnotation("runtime", String.format("%.2f", runtime * ((Montage) getApp()).getRuntimeFactor()));
                break;
            }
        }
    }
}

