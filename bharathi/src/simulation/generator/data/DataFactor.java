package simulation.generator.data;


/**
 * @author Shishir Bharathi
 */
public interface DataFactor {
    public int[] getNumberOfStageInFiles(int numFiles, int[] widths);
    public int[] getNumberOfStageOutFiles(int numFiles, int[] widths);
    public boolean needNumFiles();
}
