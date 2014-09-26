package simulation.generator.shape;

/**
 * @author Shishir Bharathi
 */
public class Constant implements Shape {
    public int[] setupWidths(int numJobs, int depth) {
        int[] widths = new int[depth];

        for (int i = 0; i < depth; i++) {
            widths[i] = numJobs / depth;
        }

        widths[0] += (numJobs % depth);

        return widths;
    }
}
