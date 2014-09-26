package simulation.generator.shape;

/**
 * @author Shishir Bharathi
 */
public interface Shape {
    public static final String DIVIDE = "DIVIDE";
    public static final String CONQUER = "CONQUER";
    public static final String DIVIDE_AND_CONQUER = "DIVIDE_AND_CONQUER";
    public static final String HOURGLASS = "HOURGLASS";

    /*
     * Determines the number of jobs at each level.
     */
    public int[] setupWidths(int numJobs, int depth);
}
