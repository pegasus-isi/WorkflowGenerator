package simulation.generator.util;

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
        for (int i = 0; i < 10; i++) {
            System.out.println("constant.getPeakMemoryConsumption(i) = " + constant.getPeakMemoryConsumption(i));
        }
    }

    @Test
    void getPeakMemoryConsumption() {
    }

}