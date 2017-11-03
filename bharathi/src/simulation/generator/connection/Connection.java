package simulation.generator.connection;


/**
 * @author Shishir Bharathi
 */
public interface Connection {
    String SPARSE = "SPARSE";
    String DENSE = "DENSE";
    String MODERATE = "MODERATE";
    /*
     * Determine the number of children for each parent.
     * NOTE: For symmetry, this may vary by +1 when creating connections.
     */
    int getNumConnections(double numParents, double numChildren);
}
