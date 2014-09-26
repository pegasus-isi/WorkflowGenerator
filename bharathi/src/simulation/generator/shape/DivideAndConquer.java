package simulation.generator.shape;

import java.util.Arrays;
import java.util.Random;


/**
 * @author Shishir Bharathi
 */
public class DivideAndConquer implements Shape {
    public int[] setupWidths(int numJobs, int depth) {
        Random random = new Random();
        int avgWidth = numJobs / depth;

        /*
         * Choose number of jobs at each level randomly.
         * Total number of jobs is set to be average width * depth.
         */
        int[] widths = new int[depth];
        int gen = ((depth % 2) == 0) ? (depth / 2) : ((depth / 2) + 1);
        int[] temp = new int[gen];

        for (int i = 0; i < gen; i++) {
            temp[i] = 1 + random.nextInt(avgWidth);
        }

        Arrays.sort(temp);

        int sum = 0;

        for (int i = 0; i < gen; i++) {
            widths[i] = temp[i];
            widths[depth - 1 - i] = temp[i];
            sum += (widths[i] * 2);
        }

        if ((depth % 2) != 0) {
            sum -= temp[gen - 1];
        }

        int remaining = numJobs - sum;

        if ((depth % 2) == 0) {
            widths[gen - 1] += (remaining / 2);
            widths[gen] += (remaining - (remaining / 2));
        } else {
            widths[gen - 1] += remaining;
        }

        return widths;
    }
}
