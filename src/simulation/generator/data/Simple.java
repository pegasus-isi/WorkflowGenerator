package simulation.generator.data;

/**
 * @author Shishir Bharathi
 */
public class Simple implements DataFactor {
    public boolean needNumFiles() {
        return true;
    }

    public int[] getNumberOfStageInFiles(int numFiles, int[] widths) {
        int[] inFiles = new int[widths.length];

        inFiles[0] = numFiles;

        for (int i = 1; i < widths.length; i++) {
            inFiles[i] = 0;
        }

        return inFiles;
    }

    public int[] getNumberOfStageOutFiles(int numFiles, int[] widths) {
        int[] outFiles = new int[widths.length];

        for (int i = 0; i < (widths.length - 1); i++) {
            outFiles[i] = 0;
        }

        outFiles[widths.length - 1] = numFiles;

        return outFiles;
    }
}
