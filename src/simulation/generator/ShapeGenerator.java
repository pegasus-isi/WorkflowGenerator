package simulation.generator;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import org.griphyn.vdl.classes.LFN;
import org.griphyn.vdl.dax.ADAG;
import org.griphyn.vdl.dax.Filename;
import org.griphyn.vdl.dax.Job;
import org.griphyn.vdl.dax.PseudoText;

import simulation.generator.connection.Connection;
import simulation.generator.connection.ConnectionFactory;
import simulation.generator.data.DataFactor;
import simulation.generator.data.DataFactorFactory;
import simulation.generator.shape.Shape;
import simulation.generator.shape.ShapeFactory;
import simulation.generator.util.Misc;

/**
 *
 * @author Shishir Bharathi
 */
public class ShapeGenerator {
    private List<List<Job>> levels;
    private ADAG dax;
    private Shape shape;
    private Connection connection;
    private DataFactor dataFactor;

    public ShapeGenerator(Shape shape, Connection connection, DataFactor dataFactor) {
        this.shape = shape;
        this.connection = connection;
        this.dataFactor = dataFactor;
        levels = new LinkedList<List<Job>>();
        this.dax = new ADAG();
    }

    private void setupLevels(int[] widths) {
        int sum = 0;

        for (int i = 0; i < widths.length; i++) {
            LinkedList<Job> level = new LinkedList<Job>();

            for (int j = 0; j < widths[i]; j++) {
                Job job = new Job("shishir", "keg", "1.0",
                        String.format("ID%05d", sum + j));
                job.addArgument(new PseudoText("-a dummy -T 60"));

                level.add(job);
                job.setLevel(i);
                this.dax.addJob(job);
            }

            sum += widths[i];
            this.levels.add(level);
        }
    }

    private void connect(Job parent, Job child, boolean data) {
        if (data) {
            /*
             * Relationship is reversed.
             * The "file" represented by the parent
             * becomes an input to the child job.
             */
            child.addUses(new Filename(parent.getName() + parent.getID(),
                    LFN.INPUT));
        } else {
            this.dax.addChild(child.getID(), parent.getID());
        }
    }

    private void connectLevels(List<Job> parents, List<Job> children, boolean data) {
        int connections = this.connection.getNumConnections(children.size(),
                parents.size());

        /*
         * Even things out if mismatched.
         * Don't have a formal proof of this yet.
         */
        if ((((connections % 2) == 0) && ((parents.size() % 2) != 0)) ||
                (((connections % 2) != 0) && ((parents.size() % 2) == 0))) {
            if (Misc.gcd(parents.size(), children.size()) == 1) {
                connections++;
            }
        }

        /*
         * Try for an even distribution of connections while ensuring
         * each parent is connected to at least one child.
         * Watch out for floating point calculations.
         */

        int target = 0;

        if ((parents.size() % 2) == 0) {
            if ((children.size() % 2) == 0) {
                target = parents.size() / 2;
            } else {
                target = (parents.size() / 2) - (connections / 2);
            }
        } else {
            if ((children.size() % 2) == 0) {
                target = (parents.size() / 2) + 1;
            } else {
                target = (parents.size() / 2) - (connections / 2);
            }
        }

        double skip = 0;

        if (children.size() > 3) {
            target -= connections;

            int factor = (children.size() / 2) - 1;

            skip = ((double) target) / factor;
        }

        /*
         * Check 0 <= skip <= connections.
         */
        if (skip > connections) {
            throw new RuntimeException("Unable to satisfy connectivity: " +
                parents.size() + " " + children.size() + " " + connections +
                " " + skip);
        } else if (skip <= 0) {
            skip = 0;
        }

        for (int i = 0; i < (children.size() / 2); i++) {
            Job child = children.get(i);

            int start = (int) Math.round(i * skip);

            for (int j = 0; j < connections; j++) {
                Job parent = parents.get(start + j);
                connect(parent, child, data);
            }

            /*
             * Mirror above relationship.
             */
            child = (Job) children.get(children.size() - 1 - i);

            for (int j = 0; j < connections; j++) {
                Job parent = parents.get(parents.size() - 1 - start - j);
                connect(parent, child, data);
            }
        }

        /*
         * The center child may not be given parents yet.
         */
        if ((children.size() % 2) != 0) {
            Job child = children.get(children.size() / 2);

            int extra = 0;

            if ((parents.size() % 2) == 0) {
                extra = 1;

                /*
                 * We have at least 2 connections as per the adjustment at
                 * the top.
                 */
                Job parent = (Job) parents.get(parents.size() / 2);
                connect(parent, child, data);
                parent = parents.get((parents.size() / 2) - 1);
                connect(parent, child, data);
                connections -= 2;
            } else {
                Job parent = (Job) parents.get(parents.size() / 2);
                connect(parent, child, data);
                if (connections % 2 != 0) {
                    connections--;
                }
            }

            for (int j = 0; j < (connections / 2); j++) {
                Job parent = parents.get((parents.size() / 2) + 1 + j);
                connect(parent, child, data);

                parent = parents.get((parents.size() / 2) - extra - 1 -
                        j);
                connect(parent, child, data);
            }
        }
    }

    private void setupDependencies() {
        for (int i = 0; i < (this.levels.size() - 1); i++) {
            List<Job> currentLevel = (List<Job>) this.levels.get(i);
            List<Job> nextLevel = (List<Job>) this.levels.get(i + 1);

            connectLevels(currentLevel, nextLevel, false);
        }
    }

    private void setupData(int numFiles, int[] widths) {
        /*
         * Create stage-in files. Dependencies may be many-to-many.
         */
        int[] inFiles = this.dataFactor.getNumberOfStageInFiles(numFiles, widths);
        int inCount = 0;

        for (int i = 0; i < widths.length; i++) {
            if (inFiles[i] > 0) {
                List<Job> level = (List<Job>) this.levels.get(i);
                List<Job> dataIn = new LinkedList<Job>();

                for (int j = 0; j < inFiles[i]; j++) {
                    Job job = new Job("dummy", "In", "1.0",
                            String.format("%05d", inCount));
                    dataIn.add(job);
                    inCount++;
                }

                /*
                 * DO NOT add job to dax.
                 */
                connectLevels(dataIn, level, true);
            }
        }

        /*
         * Create stage-out files. Here the dependencies are strictly one-to-many.
         */
        int[] outFiles = this.dataFactor.getNumberOfStageOutFiles(numFiles,
                widths);
        int outCount = 0;

        for (int i = 0; i < widths.length; i++) {
            if (outFiles[i] > 0) {
                List<Job> level = (List<Job>) levels.get(i);
                int outPerJob = outFiles[i] / level.size();

                for (int j = 0; j < level.size(); j++) {
                    Job job = (Job) level.get(j);

                    for (int k = 0; k < outPerJob; k++) {
                        job.addUses(new Filename("Out" +
                                String.format("%05d", outCount), LFN.OUTPUT));
                        outCount++;
                    }
                }

                int remaining = outFiles[i] - (outPerJob * level.size());

                /*
                 * Distribute these files as evenly as possible in the last level.
                 */
                for (int j = 0; j < remaining; j++) {
                    int index = ((level.size() - remaining) / 2) + j;
                    Job job = (Job) level.get(index);
                    job.addUses(new Filename("Out" +
                            String.format("%05d", outCount), LFN.OUTPUT));
                    outCount++;
                }
            }
        }
    }

    public void generateWorkflow(int numJobs, int numFiles, int depth)
        throws Exception {
        int[] widths = this.shape.setupWidths(numJobs, depth);

        for (int i = 0; i < widths.length; i++) {
            if (widths[i] <= 0) {
                String msg = "Error setting levels: ";

                for (int j = 0; j < widths.length; j++) {
                    msg += (" " + widths[j]);
                }

                throw new Exception(msg);
            }
        }

        setupLevels(widths);
        setupDependencies();
        setupData(numFiles, widths);
        this.dax.toXML(new OutputStreamWriter(System.out), "", null);
    }

    public static void usage() {
        String msg = "ShapeGenerator -s <shape> -c <connection> -f <data factor> -j <num jobs> -d <depth> [-h]" +
            "\n--shape | -s Shape for the workflow to be generated" +
            "\n--connection | -c Specifies how jobs in the workflow should be connected" +
            "\n--data-factor | -f Specifies distribution files to be staged in and out" +
            "\n--num-files | -F Specifies total number of files to be staged in and out" +
            "\n--num-jobs | -j Number of jobs in the workflow" +
            "\n--depth | -d Number of levels in the workflow";

        System.out.println(msg);
    }

    public static void main(String[] args) throws Exception {
        int c;
        LongOpt[] longopts = new LongOpt[7];

        longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[1] = new LongOpt("shape", LongOpt.REQUIRED_ARGUMENT, null, 's');
        longopts[2] = new LongOpt("connection", LongOpt.REQUIRED_ARGUMENT,
                null, 'c');
        longopts[3] = new LongOpt("data-factor", LongOpt.REQUIRED_ARGUMENT,
                null, 'f');
        longopts[4] = new LongOpt("num-files", LongOpt.OPTIONAL_ARGUMENT, null,
                'F');
        longopts[5] = new LongOpt("depth", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longopts[6] = new LongOpt("num-jobs", LongOpt.REQUIRED_ARGUMENT, null,
                'j');

        Getopt g = new Getopt("Generator2", args, ":c:d:f:F:hj:s:", longopts);
        g.setOpterr(false);

        Connection connection = null;
        Shape shape = null;
        DataFactor factor = null;
        int numJobs = 0;
        int numFiles = -1;
        int depth = 0;

        while ((c = g.getopt()) != -1) {
            switch (c) {
            case 'c':
                connection = ConnectionFactory.getConnection(g.getOptarg());

                break;

            case 'd':
                depth = Integer.parseInt(g.getOptarg());

                break;

            case 'f':
                factor = DataFactorFactory.getDataFactor(g.getOptarg());

                break;

            case 'F':
                numFiles = Integer.parseInt(g.getOptarg());

                break;

            case 'h':
                usage();

                break;

            case 'j':
                numJobs = Integer.parseInt(g.getOptarg());

                break;

            case 's':
                shape = ShapeFactory.getShape(g.getOptarg());

                break;

            default:
                usage();
                System.exit(1);
            }
        }

        if ((shape == null) || (connection == null) || (factor == null)) {
            usage();
            System.exit(1);
        }

        if ((numJobs == 0) || (depth == 0)) {
            usage();
            System.exit(1);
        }

        if (factor.needNumFiles() && (numFiles == -1)) {
            System.out.println(factor.getClass() +
                " needs number of files to be specified");
            System.exit(1);
        }

        ShapeGenerator generator = new ShapeGenerator(shape, connection, factor);
        generator.generateWorkflow(numJobs, numFiles, depth);
    }
}
