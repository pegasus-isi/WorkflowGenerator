package simulation.generator.connection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shishir Bharathi
 */
public class ConnectionFactory {
    private static final Map<String, Connection> map;

    static {
        map = new HashMap<String, Connection>();
        map.put(Connection.DENSE, new Dense());
        map.put(Connection.MODERATE, new Moderate());
        map.put(Connection.SPARSE, new Sparse());
        map.put("MIN", new Min());
        map.put("MAX", new Max());
    }

    public static Connection getConnection(String connection)
        throws Exception {
        Connection conn = map.get(connection.toUpperCase());

        if (conn == null) {
            throw new Exception("Connection " + connection + " not found.");
        }

        return conn;
    }
}
