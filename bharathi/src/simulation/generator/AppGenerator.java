package simulation.generator;

import simulation.generator.app.AppFactory;
import simulation.generator.app.Application;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.util.Arrays;

/**
 * Generate a workflow for a specific application and write it to stdout.
 * 
 * @author Shishir Bharathi
 */
class AppGenerator {

    private static void usage(int exitCode) {
        String msg = "AppGenerator -a <application> [-h] -- <application args>" +
                "\n--application | -a Application type." +
                "\n--help | -h Print help message.";

        System.out.println(msg);
        System.exit(exitCode);
    }

    public static void main(String[] args) throws Exception {
        LongOpt[] longopts = new LongOpt[3];

        longopts[0] = new LongOpt("application", LongOpt.REQUIRED_ARGUMENT,null, 'a');
        longopts[1] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');

        Getopt g = new Getopt("AppGenerator", args, "a:h", longopts);
        g.setOpterr(false);

        Application app = null;

        if (g.getopt() == 'a')
            app = AppFactory.getApp(g.getOptarg());
        else
            usage(1);

        String[] newArgs = Arrays.copyOfRange(args, g.getOptind(), args.length);
        app.generateWorkflow(newArgs);
        app.printWorkflow(System.out);
    }
}
