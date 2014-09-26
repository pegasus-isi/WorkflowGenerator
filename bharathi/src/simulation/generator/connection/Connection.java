package simulation.generator.connection;


/**
 * @author Shishir Bharathi
 */
public interface Connection {
    public static final String SPARSE = "SPARSE";
    public static final String DENSE = "DENSE";
    public static final String MODERATE = "MODERATE";
    /*
     * Determine the number of children for each parent.
     * NOTE: For symmetry, this may vary by +1 when creating connections.
     */
    public int getNumConnections(double numParents, double numChildren);
}
