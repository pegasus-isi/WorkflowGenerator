package simulation.generator.shape;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shishir Bharathi
 */
public class ShapeFactory {
    private static Map<String, Shape> map;

    static {
        map = new HashMap<String, Shape>();
        map.put(Shape.CONQUER, new Conquer());
        map.put(Shape.DIVIDE, new Divide());
        map.put(Shape.DIVIDE_AND_CONQUER, new DivideAndConquer());
        map.put(Shape.HOURGLASS, new HourGlass());
        map.put("MIRROR_HOURGLASS", new MirrorHourGlass());
        map.put("CONSTANT", new Constant());
    }

    public static Shape getShape(String shape) throws Exception {
        Shape sh = (Shape) map.get(shape.toUpperCase());
        
        if (sh == null) {
            throw new Exception("Shape " + shape + " not found.");
        }
        
        return sh;
    }
}
