package simulation.generator.shape;

import simulation.generator.util.Misc;

import java.util.Arrays;

/**
 * @author Shishir Bharathi
 */
public class HourGlass implements Shape {
    public int[] setupWidths(int numJobs, int depth) {
        int[] widths = new int[depth];
        
        int[] temp = Misc.nonZeroRandomSet(depth, numJobs);
        Arrays.sort(temp);
        Misc.reverse(temp, 0, depth);
        
        for (int i = 0; i < (depth / 2); i++) {
            widths[i] = temp[2 * i];
            widths[depth - 1 - i] = temp[(2 * i) + 1];
        }
        
        if ((depth % 2) != 0) {
            widths[depth / 2] = temp[depth - 1];
        }
        
        return widths;
    }
}
