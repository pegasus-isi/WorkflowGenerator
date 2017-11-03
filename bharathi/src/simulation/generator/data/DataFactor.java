package simulation.generator.data;


/**
 * @author Shishir Bharathi
 */
public interface DataFactor {
    int[] getNumberOfStageInFiles(int numFiles, int[] widths);
    int[] getNumberOfStageOutFiles(int numFiles, int[] widths);
    boolean needNumFiles();
}
