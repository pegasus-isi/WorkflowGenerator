package simulation.generator.connection;

import java.util.Random;


/**
 * @author Shishir Bharathi
 */
public class Dense implements Connection {
    /*
     * Create fewer connections between parent level and child level.
     */
    private static final Random random = new Random();

    public int getNumConnections(double numChildren, double numParents) {
        double min = numChildren / numParents;

        double r = Math.abs(random.nextGaussian());
        /*
         * r is "roughly" between 0 and 3, scale it down to [0,1].
         */
        r = ((r > 3.0) ? 3.0 : r) / 3.0;

        /*
         * Prefer values closer to max
         */
        return (int) Math.ceil(numChildren - (r * (numChildren - min)));
    }
}
