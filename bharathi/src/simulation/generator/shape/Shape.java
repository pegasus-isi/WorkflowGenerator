package simulation.generator.shape;

/**
 * @author Shishir Bharathi
 */
public interface Shape {
    String DIVIDE = "DIVIDE";
    String CONQUER = "CONQUER";
    String DIVIDE_AND_CONQUER = "DIVIDE_AND_CONQUER";
    String HOURGLASS = "HOURGLASS";

    /*
     * Determines the number of jobs at each level.
     */
    int[] setupWidths(int numJobs, int depth);
}
