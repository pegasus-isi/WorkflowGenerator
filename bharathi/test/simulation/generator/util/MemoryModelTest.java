package simulation.generator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Carl Witt on 13.11.17.
 *
 * @author Carl Witt (cpw@posteo.de)
 */
class MemoryModelTest {


    @Test
    void constant() {
        MemoryModel constant = MemoryModel.constant(100, 10);
        for (int i = 0; i < 10000; i++) {
            long first = constant.getPeakMemoryConsumption(i);
            long second = constant.getPeakMemoryConsumption(i);
            // assert deterministic error
            Assertions.assertEquals(first, second);
            // assert range
            Assertions.assertTrue(90 <= first && first <= 110);
        }
    }

    @Test
    void getPeakMemoryConsumption() {
        MemoryModel linear = new MemoryModel(2d, 10d, 20);
        for (int i = 0; i < 10000; i++) {
            long first = linear.getPeakMemoryConsumption(i);
            long second = linear.getPeakMemoryConsumption(i);
            // assert deterministic error
            Assertions.assertEquals(first, second);
            // assert range
            Assertions.assertTrue(2*i+10 - 20 <= first && first <= 2*i+10 + 20);
            // assert non-negativity
            Assertions.assertTrue(first >= 0);
        }
    }

}