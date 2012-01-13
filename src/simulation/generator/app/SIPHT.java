package simulation.generator.app;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.Set;

import simulation.generator.util.Distribution;
import simulation.generator.util.Misc;

/**
 * @author Shishir Bharathi
 */
public class SIPHT extends AbstractApplication {

    public static final String NAMESPACE = "SIPHT";
    public static final String CODE = "NC_0025AG05";
    private static final int MEAN_PATSERS = 18;
    private int PARTNER_FACTOR = 936;
    private double runtimeFactor = 1.0;
    private int numJobs;

    protected void populateDistributions() {
        /*
         * Loads default distributions for all variables used in the
         * generation of the workflow.
         * Distributions for variables used by all jobs are collected here.
         * Eventually, support picking this information from external source.
         */
        
        this.distributions.put("IGR_partners", Distribution.getUniformDistribution(0.9 * PARTNER_FACTOR, PARTNER_FACTOR));
        double FNA_SIZE = Misc.truncatedNormal(5248967.25, 2301068882937.69);
        this.distributions.put("CODE.fna", Distribution.getConstantDistribution(FNA_SIZE));

        this.distributions.put("RNAfold",
                Distribution.getConstantDistribution(591942));
        this.distributions.put("findterm.out", Distribution.getConstantDistribution(134));
        this.distributions.put("findterm.err", Distribution.getConstantDistribution(0));
        this.distributions.put("CODE_term.txt", Distribution.getTruncatedNormalDistribution(16463275, 95617942503538.19));
        this.distributions.put("CODE_term_candidates", Distribution.getTruncatedNormalDistribution(811130.00, 1348116307840.50));
        this.distributions.put("CODE_term_candidate_nonredund", Distribution.getTruncatedNormalDistribution(385620, 15568279781.19));
        this.distributions.put("rna.ps", Distribution.getTruncatedNormalDistribution(2837.50, 512.25));
        this.distributions.put("vienna_input_tmp", Distribution.getTruncatedNormalDistribution(3974004.00, 849579407883.00));
        this.distributions.put("vienna_output", Distribution.getTruncatedNormalDistribution(7729972.50, 23454353107374.75));
        this.distributions.put("vienna_index_tmp", Distribution.getTruncatedNormalDistribution(5827902.00, 1670001877413.50));

        this.distributions.put("RNAMofficial_descriptor.txt", Distribution.getConstantDistribution(17626));
        this.distributions.put("rnamotif.out", Distribution.getTruncatedNormalDistribution(1053510.50, 8411320934.75));
        this.distributions.put("rnamotif.err", Distribution.getConstantDistribution(310.0));

        this.distributions.put("expterm.dat", Distribution.getConstantDistribution(45788));
        double CODE_PTT = Misc.truncatedNormal(328510.50, 76625546.75);
        this.distributions.put("CODE.ptt", Distribution.getConstantDistribution(CODE_PTT));
        this.distributions.put("transterm.out", Distribution.getTruncatedNormalDistribution(440409.25, 211951229.69));
        this.distributions.put("transterm.err", Distribution.getTruncatedNormalDistribution(96.25, 0.19));

        this.distributions.put("blasta", Distribution.getConstantDistribution(786856));
        this.distributions.put("xdformat", Distribution.getConstantDistribution(380164));
        this.distributions.put("time", Distribution.getConstantDistribution(11944));
        this.distributions.put("CODE_IGR_partners.txt", Distribution.getTruncatedNormalDistribution(288565.20, 107942475771.75));
        this.distributions.put("blast.err", Distribution.getTruncatedNormalDistribution(548.75, 0.19));
        this.distributions.put("blast.out", Distribution.getTruncatedNormalDistribution(511.75, 0.19));
        this.distributions.put("BLAST_sorted.out", Distribution.getTruncatedNormalDistribution(5270722.25, 11857591385947.69));

        this.distributions.put("alphabet", Distribution.getConstantDistribution(16));
        this.distributions.put("matrix", Distribution.getConstantDistribution(170917));
        this.distributions.put("patser.in", Distribution.getConstantDistribution(14));
        this.distributions.put("CODE_PatserOut.txt", Distribution.getTruncatedNormalDistribution(91780.15, 61994599624.9));

        this.distributions.put("access_genomes2.txt", Distribution.getConstantDistribution(83722));
        this.distributions.put("sRNAPredict.in", Distribution.getConstantDistribution(1198));
        this.distributions.put("CODE.gbk", Distribution.getTruncatedNormalDistribution(11664926.25, 5027367745576.69));

        this.distributions.put("All_known_sRNAs.txt", Distribution.getConstantDistribution(1028403));
        this.distributions.put("srna.err", Distribution.getTruncatedNormalDistribution(1859, 36));
        this.distributions.put("Blast_Out", Distribution.getTruncatedNormalDistribution(2491818.25, 2780189305579.69));
        this.distributions.put("OutBlastParsed", Distribution.getTruncatedNormalDistribution(1002636.75, 995765999448.69));
        this.distributions.put("OutCandidates", Distribution.getTruncatedNormalDistribution(222136.25, 9508831415.85));
        this.distributions.put("OutConsIG", Distribution.getTruncatedNormalDistribution(422672.75, 60310327140.69));
        this.distributions.put("OutIG", Distribution.getTruncatedNormalDistribution(605995, 103944177));
        this.distributions.put("OutORF", Distribution.getTruncatedNormalDistribution(328412.5, 559717064.75));
        this.distributions.put("OutTerm_temp", Distribution.getTruncatedNormalDistribution(115600.75, 1330343466.19));
        this.distributions.put("OutTerms", Distribution.getTruncatedNormalDistribution(53733.5, 184633890.75));
        this.distributions.put("OutTermsIG", Distribution.getTruncatedNormalDistribution(18803.75, 23633877.69));
        this.distributions.put("srna.out", Distribution.getTruncatedNormalDistribution(331823.5, 482134362.75));
        this.distributions.put("CODE.ffn", Distribution.getTruncatedNormalDistribution(4202875, 168713989549.5));
        this.distributions.put("Seq_CODE", Distribution.getTruncatedNormalDistribution(84073, 12298824));
        this.distributions.put("CODE_parsed.ffn", Distribution.getTruncatedNormalDistribution(802506.75, 18062265579.69));
        this.distributions.put("Seq_known_sRNAs_IGRs.txt", Distribution.getConstantDistribution(172432));
        double BLAST_CODE = Misc.truncatedNormal(11427.25, 21904571.69);
        this.distributions.put("BLAST_CODE", Distribution.getConstantDistribution(BLAST_CODE));
        this.distributions.put("blast_candidate.out", Distribution.getTruncatedNormalDistribution(2965.5, 24644.25));
        this.distributions.put("blast_candidate.err", Distribution.getTruncatedNormalDistribution(544.75, 0.19));

        this.distributions.put("mix_tied_linux.cfg", Distribution.getConstantDistribution(11964));
        this.distributions.put("RIBOPROB.mat", Distribution.getConstantDistribution(8350));
        this.distributions.put("BLOSUM62", Distribution.getConstantDistribution(2122));
        this.distributions.put("blastn2qrnadepth.pl", Distribution.getConstantDistribution(69426));
        this.distributions.put("qrna2gff.pl", Distribution.getConstantDistribution(16995));
        this.distributions.put("eqrna", Distribution.getConstantDistribution(953556));
        this.distributions.put("qrna.err", Distribution.getConstantDistribution(610));
        this.distributions.put("qrna.out", Distribution.getTruncatedNormalDistribution(4809.62, 9530118.98));
        this.distributions.put("CODE_QRNA.txt", Distribution.getTruncatedNormalDistribution(773386.5, 201840720.75));
        this.distributions.put("CODE_QRNAblast.txt", Distribution.getTruncatedNormalDistribution(3115449.75, 433512921497.19));
        this.distributions.put("CODE_QRNAblast.txt.E0.01.D1.q", Distribution.getTruncatedNormalDistribution(163635, 103267109.5));
        this.distributions.put("CODE_QRNAblast.txt.E0.01.D1.q.gff", Distribution.getTruncatedNormalDistribution(38934, 47855409.5));
        this.distributions.put("CODE_QRNAblast.txt.E0.01.D1.q.rep", Distribution.getTruncatedNormalDistribution(39147.75, 62250756.19));
        this.distributions.put("CODE_QRNA.txt.all.CUTOFF0.ID[100:0].GC[100:0].gff", Distribution.getTruncatedNormalDistribution(175489.75, 90766297.69));
        this.distributions.put("Flanking_ORFs_known.txt", Distribution.getConstantDistribution(1419426));
        this.distributions.put("CODE_synteny.txt", Distribution.getTruncatedNormalDistribution(1146801.25, 445588527066.69));
        this.distributions.put("CODE_paralogues_temp.txt", Distribution.getTruncatedNormalDistribution(90154.75, 6814689167.19));
        this.distributions.put("synteny_temp.txt", Distribution.getTruncatedNormalDistribution(20552, 133473984.5));
        this.distributions.put("synteny_temp2.txt", Distribution.getTruncatedNormalDistribution(2706.5, 2262206.25));
        this.distributions.put("Block0_Parsed_temp_CODE_PatserOut.txt", Distribution.getTruncatedNormalDistribution(567813.75, 880488134611.69));
        this.distributions.put("QRNA_out", Distribution.getTruncatedNormalDistribution(6572.75, 12596574.19));
        this.distributions.put("srna_annotate.err", Distribution.getTruncatedNormalDistribution(1070.5, .75));
        this.distributions.put("srna_annotate.out", Distribution.getTruncatedNormalDistribution(565462, 98726501739.5));

        double CODE_SRNA_OUT_ANNOTATED = Misc.truncatedNormal(307952.25, 3812001448.19);
        this.distributions.put("CODE_sRNA.out_annotated", Distribution.getConstantDistribution(CODE_SRNA_OUT_ANNOTATED));

        this.distributions.put("CODE_paralogues.txt", Distribution.getTruncatedNormalDistribution(690731.5, 362706549090.75));

        this.distributions.put("Findterm", Distribution.getTruncatedNormalDistribution(1349.47, 635206.26));
        this.distributions.put("Findterm_mean", Distribution.getConstantDistribution(1349.47));
        this.distributions.put("Transterm", Distribution.getTruncatedNormalDistribution(55.78, 2356.11));
        this.distributions.put("RNAMotif", Distribution.getTruncatedNormalDistribution(36.42, 78.15));
        this.distributions.put("Blast", Distribution.getTruncatedNormalDistribution(2350.58, 1033834.66));
        this.distributions.put("FFN_parse", Distribution.getTruncatedNormalDistribution(1.64, 0.06));
        this.distributions.put("SRNA", Distribution.getTruncatedNormalDistribution(361.33, 117451.46));
        this.distributions.put("Blast_paralogues", Distribution.getTruncatedNormalDistribution(4.99, 1.32));
        this.distributions.put("Blast_candidate", Distribution.getTruncatedNormalDistribution(5.18, 0.99));
        this.distributions.put("Blast_QRNA", Distribution.getTruncatedNormalDistribution(1412.09, 9702.26));
        this.distributions.put("Patser", Distribution.getTruncatedNormalDistribution(1.27, 0.11));
        this.distributions.put("Patser_concate", Distribution.getTruncatedNormalDistribution(0.08, 0.01));
        this.distributions.put("SRNA_annotate", Distribution.getTruncatedNormalDistribution(1.68, 0.92));
        this.distributions.put("Blast_synteny", Distribution.getConstantDistribution(33.0));
    }

    private void usage(int exitCode) {
        String msg = "SIPHT [-h] [options]" +
                "\n--factor | -f Avg. runtime to execute an Blast_candidate job" +
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
        LongOpt[] longopts = new LongOpt[3];

        longopts[0] = new LongOpt("factor", LongOpt.REQUIRED_ARGUMENT, null, 'f');
        longopts[1] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[2] = new LongOpt("num-jobs", LongOpt.REQUIRED_ARGUMENT, null, 'n');

        Getopt g = new Getopt("SIPHT", args, "f:hn:", longopts);
        g.setOpterr(false);
        
        double factor = 1.0;
        int numJobs = 0;

        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'f':
                    factor = Double.parseDouble(g.getOptarg());
                    this.runtimeFactor = factor / generateDouble("Findterm_mean");
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


        if (numJobs > 0) {
            this.numJobs = numJobs;
        } else {
            usage(1);
        }

    }

    protected void constructWorkflow() {
        if (numJobs < 30) {
            throw new RuntimeException("Too few jobs: " + numJobs);
        }
        int count = (int) Math.round((double) numJobs / (MEAN_PATSERS + 13));
        
        int remaining = numJobs - count * 13;
        int[] countJobs = Misc.closeNonZeroRandoms(count, remaining, 0.2);
        for (int i = 0; i < countJobs.length; i++) {
            constructSubWorkflow(countJobs[i]);
        }
    }

    protected void constructSubWorkflow(int remaining) {
        if (remaining <= 0) {
            throw new RuntimeException("Cannot construct workflow with numJobs=" + (remaining + 13));
        }

        PatserConcate pc = new PatserConcate(this, "Patser_concate", "1.0", getNewJobID());
        for (int i = 0; i < remaining; i++) {
            Patser p = new Patser(this, "Patser", "1.0", getNewJobID());
            p.addChild(pc);
        }

        Findterm findTerm = new Findterm(this, "Findterm", "1.0", getNewJobID());
        RNAMotif rnaMotif = new RNAMotif(this, "RNAMotif", "1.0", getNewJobID());
        Transterm transterm = new Transterm(this, "Transterm", "1.0", getNewJobID());
        Blast blast = new Blast(this, "Blast", "1.0", getNewJobID());

        SRNA srna = new SRNA(this, "SRNA", "1.0", getNewJobID());
        findTerm.addChild(srna);
        rnaMotif.addChild(srna);
        transterm.addChild(srna);
        blast.addChild(srna);

        FFNParse ffnParse = new FFNParse(this, "FFN_Parse", "1.0", getNewJobID());
        srna.addChild(ffnParse);

        BlastCandidate bc = new BlastCandidate(this, "Blast_candidate", "1.0", getNewJobID());
        BlastQRNA bq = new BlastQRNA(this, "Blast_QRNA", "1.0", getNewJobID());
        BlastSynteny bs = new BlastSynteny(this, "Blast_synteny", "1.0", getNewJobID());
        BlastParalogues bp = new BlastParalogues(this, "Blast_paralogues", "1.0", getNewJobID());

        srna.addChild(bc);
        srna.addChild(bq);
        srna.addChild(bs);
        srna.addChild(bp);

        ffnParse.addChild(bs);

        SRNAAnnotate srnaAnnotate = new SRNAAnnotate(this, "SRNA_annotate", "1.0", getNewJobID());
        srna.addChild(srnaAnnotate);
        pc.addChild(srnaAnnotate);
        bc.addChild(srnaAnnotate);
        bq.addChild(srnaAnnotate);
        bs.addChild(srnaAnnotate);
        bp.addChild(srnaAnnotate);
    }
}

class Findterm extends AppJob {

    public Findterm(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input(SIPHT.CODE + ".fna", sipht.generateLong("CODE.fna"));
        input("RNAfold", sipht.generateLong("RNAfold"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Findterm") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        SIPHT sipht = (SIPHT) getApp();
        addLink(child, "findterm.out", sipht.generateLong("findterm.out"));
        addLink(child, "findterm.err", sipht.generateLong("findterm.err"));
        addLink(child, SIPHT.CODE + "_term.txt", sipht.generateLong("CODE_term.txt"));
        addLink(child, SIPHT.CODE + "_term_candidates_non_redund",
                sipht.generateLong("CODE_term_candidate_nonredund"));
        addLink(child, "rna.ps", sipht.generateLong("rna.ps"));
        addLink(child, "vienna_input_tmp", sipht.generateLong("vienna_input_tmp"));
        addLink(child, "vienna_output", sipht.generateLong("vienna_output"));
        addLink(child, "vienna_index_tmp", sipht.generateLong("vienna_index_tmp"));
    }
}

class RNAMotif extends AppJob {

    public RNAMotif(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input(SIPHT.CODE + ".fna", sipht.generateLong("CODE.fna"));
        input("RNAMofficial_descriptor.txt", sipht.generateLong("RNAMofficial_descriptor.txt"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("RNAMotif") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        SIPHT sipht = (SIPHT) getApp();
        addLink(child, "rnamotif.out", sipht.generateLong("rnamotif.out"));
        addLink(child, "rnamotif.err", sipht.generateLong("rnamotif.err"));
    }
}

class Transterm extends AppJob {

    public Transterm(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name,
                version, jobID);
        input(SIPHT.CODE + ".fna", sipht.generateLong("CODE.fna"));
        input("expterm.dat", sipht.generateLong("expterm.dat"));
        input(SIPHT.CODE + ".ptt", sipht.generateLong("CODE.ptt"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Transterm") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        SIPHT sipht = (SIPHT) getApp();
        addLink(child, "transterm.out",
                sipht.generateLong("transterm.out"));
        addLink(child, "transterm.err",
                sipht.generateLong("transterm.err"));
    }
}

class Blast extends AppJob {

    public Blast(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input("blasta", sipht.generateLong("blasta"));
        input("xdformat", sipht.generateLong("xdformat"));
        input("time", sipht.generateLong("time"));
        input(SIPHT.CODE + "_IGR_partners.txt",
                sipht.generateLong("CODE_IGR_partners.txt"));

        int partnersCount = sipht.generateInt("IGR_partners");
        for (int i = 0; i < partnersCount; i++) {
            input("NC_" + i + "_IGR_partners.txt",
                    sipht.generateLong("CODE_IGR_partners.txt"));
        }
        output("blast.err", sipht.generateLong("blast.err"));

        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Blast") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        SIPHT sipht = (SIPHT) getApp();
        addLink(child, "blast.out", sipht.generateLong("blast.out"));
        addLink(child, "BLAST_sorted.out",
                sipht.generateLong("BLAST_sorted.out"));
    }
}

class Patser extends AppJob {

    private String jobID;

    public Patser(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        this.jobID = jobID;
        input("patser.in", sipht.generateLong("patser.in"));
        input(SIPHT.CODE + ".fna", sipht.generateLong("CODE.fna"));
        input("alphabet", sipht.generateLong("alphabet"));
        input(jobID + "_matrix.txt", sipht.generateLong("matrix"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Patser") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        SIPHT sipht = (SIPHT) getApp();
        addLink(child, SIPHT.CODE + "_PatserOut" + jobID + ".txt",
                sipht.generateLong("CODE_PatserOut.txt"));
    }
}

class PatserConcate extends AppJob {

    public PatserConcate(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Patser_concate") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();

        long size = 0;
        for (AppFilename input : inputs) {
            size += input.getSize();
        }
        addLink(child, SIPHT.CODE + "_PatserOut.txt", size);
    }
}

class SRNA extends AppJob {

    public SRNA(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input("access_genomes2.txt", sipht.generateLong("access_genomes2.txt"));
        input("sRNAPredict.in", sipht.generateLong("sRNAPredict.in"));
        input(SIPHT.CODE + ".gbk", sipht.generateLong("CODE.gbk"));
        input(SIPHT.CODE + ".fna", sipht.generateLong("CODE.fna"));
        input(SIPHT.CODE + ".ptt", sipht.generateLong("CODE.ptt"));
        input("All_known_sRNAs.txt", sipht.generateLong("All_known_sRNAs.txt"));
        output("srna.err", sipht.generateLong("srna.err"));
        output("Blast_Out", sipht.generateLong("Blast_Out"));
        output("OutBlastParsed", sipht.generateLong("OutBlastParsed"));
        output("OutCandidates", sipht.generateLong("OutCandidates"));
        output("OutCandidates2", sipht.generateLong("OutCandidates"));
        output("OutCandidates3", sipht.generateLong("OutCandidates"));
        output("OutConsIG", sipht.generateLong("OutConsIG"));
        output("OutIG", sipht.generateLong("OutIG"));
        output("OutORF", sipht.generateLong("OutORF"));
        output("OutTerm_temp", sipht.generateLong("OutTerm_temp"));
        output("OutTerms", sipht.generateLong("OutTerms"));
        output("OutTermsIG", sipht.generateLong("OutTermsIG"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("SRNA") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        addLink(child, "Seq_" + SIPHT.CODE, ((SIPHT) getApp()).generateLong("Seq_CODE"));
        if (child instanceof SRNAAnnotate) {
            addLink(child, "srna.out", ((SIPHT) getApp()).generateLong("srna.out"));
        }
    }
}

class FFNParse extends AppJob {

    public FFNParse(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input(SIPHT.CODE + ".ffn", sipht.generateLong("CODE.ffn"));
        input(SIPHT.CODE + ".ptt", sipht.generateLong("CODE.ptt"));
        input("Seq_" + SIPHT.CODE, sipht.generateLong("Seq_CODE"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("FFN_parse") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        addLink(child, SIPHT.CODE + "_parsed.ffn", ((SIPHT) getApp()).generateLong("CODE_parsed.ffn"));
    }
}

class BlastCandidate extends AppJob {

    public BlastCandidate(SIPHT sipht, String name, String version, String jobID) {
        super(sipht,
                SIPHT.NAMESPACE, name, version, jobID);
        input("Seq_known_sRNAs_IGRs.txt", sipht.generateLong("Seq_known_sRNAs_IGRs.txt"));
        input("blasta", sipht.generateLong("blasta"));
        input("xdformat", sipht.generateLong("xdformat"));
        input("time", sipht.generateLong("time"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Blast_candidate") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        addLink(child,
                "BLAST_" + SIPHT.CODE, ((SIPHT) getApp()).generateLong("BLAST_CODE"));
        addLink(child, "blast_candidate.out", ((SIPHT) getApp()).generateLong("blast_candidate.out"));
        addLink(child, "blast_candidate.err", ((SIPHT) getApp()).generateLong("blast_candidate.err"));
    }
}

class BlastQRNA extends AppJob {

    public BlastQRNA(SIPHT sipht, String name, String version, String jobID) {

        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input("xdformat", sipht.generateLong("xdformat"));
        input("blasta", sipht.generateLong("blasta"));
        input("time", sipht.generateLong("time"));
        input("mix_tied_linux.cfg", sipht.generateLong("mix_tied_linux.cfg"));
        input("RIBOPROB.mat", sipht.generateLong("RIBOPROB.mat"));
        input("BLOSUM62", sipht.generateLong("BLOSUM62"));
        input("blastn2qrnadepth.pl", sipht.generateLong("blastn2qrnadepth.pl"));
        input("qrna2gff.pl", sipht.generateLong("qrna2gff.pl"));
        input("eqrna", sipht.generateLong("eqrna"));
        input(SIPHT.CODE + "_IGR_partners.txt", sipht.generateLong("CODE_IGR_partners.txt"));
        
        int partnersCount = sipht.generateInt("IGR_partners");
        for (int i = 0; i < partnersCount;
                i++) {
            input("NC_" + i + "_IGR_partners.txt", sipht.generateLong("CODE_IGR_partners.txt"));
        }
        output("qrna.err", sipht.generateLong("qrna.err"));
        output("qrna.out", sipht.generateLong("qrna.out"));
        output(SIPHT.CODE + "_QRNA.txt", sipht.generateLong("CODE_QRNA.txt"));
        output(SIPHT.CODE + "_QRNAblast.txt", sipht.generateLong("CODE_QRNAblast.txt"));
        output(SIPHT.CODE + "_QRNAblast.txt.E0.01.D1.q", sipht.generateLong("CODE_QRNAblast.txt.E0.01.D1.q"));
        output(SIPHT.CODE + "_QRNAblast.txt.E0.01.D1.q.gff", sipht.generateLong("CODE_QRNAblast.txt.E0.01.D1.q.gff"));
        output(SIPHT.CODE + "_QRNAblast.txt.E0.01.D1.q.rep", sipht.generateLong("CODE_QRNAblast.txt.E0.01.D1.q.rep"));

        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Blast_QRNA") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        addLink(child, SIPHT.CODE + "_QRNA.txt.all.CUTOFF0.ID[100:0].GC[100:0].gff",
                ((SIPHT) getApp()).generateLong("CODE_QRNA.txt.all.CUTOFF0.ID[100:0].GC[100:0].gff"));
    }
}

class BlastSynteny extends AppJob {

    public BlastSynteny(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input("Flanking_ORFs_known.txt", sipht.generateLong("Flanking_ORFs_known.txt"));
        input("xdformat", sipht.generateLong("xdformat"));
        input("blasta", sipht.generateLong("blasta"));
        input("time", sipht.generateLong("time"));

        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Blast_synteny") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        addLink(child, SIPHT.CODE + "_synteny.txt", ((SIPHT) getApp()).generateLong("CODE_synteny.txt"));
    }
}

class SRNAAnnotate extends AppJob {

    public SRNAAnnotate(SIPHT sipht, String name, String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input("access_matrix.txt", sipht.generateInt("matrix"));
        input("sRNAPredict.in", sipht.generateLong("sRNAPredict.in"));
        input("BLAST_" + SIPHT.CODE, sipht.generateLong("BLAST_CODE"));
        output(SIPHT.CODE + "_paralogues_temp.txt", sipht.generateLong("CODE_paralogues_temp.txt"));
        output("synteny_temp.txt", sipht.generateLong("synteny_temp.txt"));
        output("synteny_temp2.txt", sipht.generateLong("synteny_temp2.txt"));
        output("Block0_Parsed_temp_" + SIPHT.CODE + "_PatserOut.txt", sipht.generateLong("Block0_Parsed_temp_CODE_PatserOut.txt"));
        output("QRNA_out", sipht.generateLong("QRNA_out"));
        output("srna_annotate.err", sipht.generateLong("srna_annotate.err"));
        output("srna_annotate.out", sipht.generateLong("srna_annotate.out"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("SRNA_annotate") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        addLink(child, SIPHT.CODE + "_sRNA.out_annotated", ((SIPHT) getApp()).generateLong("CODE_sRNA.out_annotated"));
    }
}

class BlastParalogues extends AppJob {

    public BlastParalogues(SIPHT sipht, String name,
            String version, String jobID) {
        super(sipht, SIPHT.NAMESPACE, name, version, jobID);
        input("xdformat", sipht.generateLong("xdformat"));
        input("blasta", sipht.generateLong("blasta"));
        input("time", sipht.generateLong("time"));
        addAnnotation("runtime", String.format("%.4f", sipht.generateDouble("Blast_paralogues") * sipht.getRuntimeFactor()));
    }

    public void addChild(AppJob child) {
        addLink(child, SIPHT.CODE + "_paralogues.txt", ((SIPHT) getApp()).generateLong("CODE_paralogues.txt"));
    }
}
