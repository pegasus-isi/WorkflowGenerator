package simulation.generator.connection;

import java.util.Random;


/**
 * @author Shishir Bharathi
 */
public class Sparse implements Connection {
    /*
     * Create fewer connections between parent level and child level.
     */
    private static Random random = new Random();

    public int getNumConnections(double numChildren, double numParents) {
        double min = numChildren / numParents;
        double max = numChildren;

        double r = Math.abs(random.nextGaussian());
        /*
         * r is "roughly" between 0 and 3, scale it down to [0,1].
         */
        r = ((r > 3.0) ? 3.0 : r) / 3.0;

        /*
         * Prefer values closer to min
         */
        return (int) Math.ceil(min + (r * (max - min)));
    }
}
