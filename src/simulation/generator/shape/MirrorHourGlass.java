package simulation.generator.shape;

import simulation.generator.util.Misc;

import java.util.Arrays;

/**
 * @author Shishir Bharathi
 */
public class MirrorHourGlass implements Shape {
    public int[] setupWidths(int numJobs, int depth) {
        int[] widths = new int[depth];
        
        int gen = ((depth % 2) == 0) ? (depth / 2) : ((depth / 2) + 1);
        int[] temp = Misc.nonZeroRandomSet(gen, numJobs / 2);
        Arrays.sort(temp);
        Misc.reverse(temp, 0, gen);
        
        for (int i = 0; i < gen; i++) {
            widths[i] = temp[i];
            widths[depth - 1 - i] = widths[i];
        }
        
        if ((depth % 2) != 0) {
            widths[depth / 2] = temp[gen - 1];
            /*
             * temp[gen -1] is not doubled earlier.
             */
            widths[0] += temp[gen - 1];
        }
        
        /*
         * We may still be off by 1.
         */
        if ((numJobs % 2) != 0) {
            widths[0]++;
        }
        
        return widths;
    }
}
