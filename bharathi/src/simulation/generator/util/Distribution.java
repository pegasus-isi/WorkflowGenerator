package simulation.generator.util;

/**
 * @author Shishir Bharathi
 */
public abstract class Distribution {

    public enum TYPE {
        CONSTANT,
        UNIFORM,
        TRUNCATED_NORMAL,
    };

    public static Distribution getConstantDistribution(double value) {
        return getConstantDistribution(value, 1.0);
    }

    public static Distribution getConstantDistribution(double value, double scalingFactor) {
        return new ConstantDistribution(value, scalingFactor);
    }

    public static Distribution getUniformDistribution(double min, double max) {
        return getUniformDistribution(min, max, 1.0);
    }

    public static Distribution getUniformDistribution(double min, double max, double scalingFactor) {
        return new UniformDistribution(min, max, scalingFactor);
    }

    public static Distribution getTruncatedNormalDistribution(double mean, double variance) {
        return getTruncatedNormalDistribution(mean, variance, 1.0);
    }

    public static Distribution getTruncatedNormalDistribution(double mean, double variance, double scalingFactor) {
        return new TruncatedNormalDistribution(mean, variance, scalingFactor);
    }

    public abstract double getDouble();

    public int getInt() {
        return (int) Math.ceil(getDouble());
    }

    public long getLong() {
        return (long) Math.ceil(getDouble());
    }
}

class TruncatedNormalDistribution extends Distribution {

    private final double mean;
    private final double variance;
    private final double scalingFactor;

    public TruncatedNormalDistribution(double mean, double variance, double scalingFactor) {
        this.mean = mean;
        this.variance = variance;
        this.scalingFactor = scalingFactor;
    }

    public double getDouble() {
        return Misc.truncatedNormal(mean * scalingFactor, variance * scalingFactor * scalingFactor);
    }
}

class ConstantDistribution extends Distribution {

    private final double value;

    /*
     * We don't really need to separate value and scalingFactor for
     * Constant, but this is more informative.
     */
    public ConstantDistribution(double value, double scalingFactor) {
        /*
         * Ingore variance and factor
         */
        this.value = value * scalingFactor;
    }

    public double getDouble() {
        return this.value;
    }
}

class UniformDistribution extends Distribution {

    private final double min;
    private final double max;
    private final double scalingFactor;

    public UniformDistribution(double min, double max, double scalingFactor) {
        this.min = min;
        this.max = max;
        this.scalingFactor = scalingFactor;
    }

    public double getDouble() {
        return Misc.randomDouble(min * scalingFactor, max * scalingFactor);
    }
}