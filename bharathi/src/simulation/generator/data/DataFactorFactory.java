package simulation.generator.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shishir Bharathi
 */
public class DataFactorFactory {
    public static final String FIXED_1 = "FIXED_1";
    public static final String FIXED_7 = "FIXED_2";
    public static final String FIXED_10 = "FIXED_10";
    public static final String BIASED_RANDOM_1000 = "BIASED_RANDOM_1000";
    public static final String BIASED_RANDOM_10000 = "BIASED_RANDOM_10000";
    public static final String SIMPLE = "SIMPLE";
    private static Map<String, DataFactor> map;

    static {
        map = new HashMap<String, DataFactor>();
        map.put(FIXED_1, new Fixed(1));
        map.put(FIXED_7, new Fixed(7));
        map.put(FIXED_10, new Fixed(10));
        map.put(BIASED_RANDOM_1000, new BiasedRandom(1000));
        map.put(BIASED_RANDOM_10000, new BiasedRandom(10000));
        map.put(SIMPLE, new Simple());
    }

    public static DataFactor getDataFactor(String dataFactor)
        throws Exception {
        DataFactor df = (DataFactor) map.get(dataFactor.toUpperCase());

        if (df == null) {
            throw new Exception("DataFactor " + dataFactor + " not found.");
        }

        return df;
    }
}
