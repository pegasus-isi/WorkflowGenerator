package simulation.generator.connection;

/**
 * @author Shishir Bharathi
 */
public class Min implements Connection {
    /*
     * Create fewer connections between parent level and child level.
     */
    public int getNumConnections(double numChildren, double numParents) {
        return (int) Math.ceil(numParents / numChildren);
    }
}
