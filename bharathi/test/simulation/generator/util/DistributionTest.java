package simulation.generator.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Carl Witt on 12.11.17.
 *
 * @author Carl Witt (cpw@posteo.de)
 */
class DistributionTest {
    @Test
    void getConstantDistribution() {
        Distribution d = Distribution.getConstantDistribution(100);
        for (int i = 0; i < 10; i++) {
            System.out.println(d.getDouble());
        }
    }

    @Test
    void getUniformDistribution() {
        Distribution d = Distribution.getUniformDistribution(50, 100, 1);
        for (int i = 0; i < 1000; i++) {
            System.out.println(d.getDouble());
        }
    }

    @Test
    void getTruncatedNormalDistribution() {
        // truncated normal is strangely defined, truncated means reject all samples that are outside µ ± 0.5µ
        // I expected it to reject all samples < 0
        Distribution d = Distribution.getTruncatedNormalDistribution(100, 25 * 25, 1);
        for (int i = 0; i < 1000; i++) {
            System.out.println(d.getDouble());
        }
    }

}