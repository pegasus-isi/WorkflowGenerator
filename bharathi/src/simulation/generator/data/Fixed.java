package simulation.generator.data;

/**
 * @author Shishir Bharathi
 */
public class Fixed implements DataFactor {
    private int factor;

    public Fixed(int factor) {
        this.factor = factor;
    }

    public boolean needNumFiles() {
        return false;
    }

    public int[] getNumberOfStageInFiles(int numFiles, int[] widths) {
        int[] inFiles = new int[widths.length];

        for (int i = 0; i < widths.length; i++) {
            inFiles[i] = widths[i] * factor;
        }

        return inFiles;
    }

    public int[] getNumberOfStageOutFiles(int numFiles, int[] widths) {
        int[] outFiles = new int[widths.length];

        for (int i = 0; i < widths.length; i++) {
            outFiles[i] = widths[i] * factor;
        }

        return outFiles;
    }
}
