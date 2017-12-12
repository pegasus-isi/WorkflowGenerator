package simulation.generator.connection;

import java.util.Random;


/**
 * @author Shishir Bharathi
 */
public class Moderate implements Connection {
    /*
     * Create fewer connections between parent level and child level.
     */
    private static final Random random = new Random();

    public int getNumConnections(double numChildren, double numParents) {
        double min = numChildren / numParents;

        double r = random.nextGaussian();

        /*
         * r is "roughly" between -3 and 3, scale it down to [-1,1].
         */
        int rsgn = (r >= 0) ? 1 : (-1);
        r = ((Math.abs(r) > 3.0) ? (3.0 * rsgn) : r) / 3.0;

        /*
         * Prefer values closer to (min + max)/2
         * = (min + max + min - min)/2
         * = min + (max - min)/2
         */
        return (int) Math.ceil(min + ((r * (numChildren - min)) / 2));
    }
}
