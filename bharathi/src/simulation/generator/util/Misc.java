package simulation.generator.util;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Shishir Bharathi
 * @author Gideon Juve <juve@usc.edu>
 */
public class Misc {

    private static final Random random;
    private static final int MAX_TRIES = 100;
    
    static {
        random = new Random();
    }

    /**
     * Calculate gcd of two non-negative integers.
     */
    public static int gcd(int a, int b) {
        if ((a < 0) || (b < 0)) {
            throw new IllegalArgumentException("Negative numbers not allowed.");
        }

        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }

    /**
     * Calculate lcm.
     */
    public static int lcm(int a, int b) {
        if ((a < 0) || (b < 0)) {
            throw new IllegalArgumentException("Negative numbers not allowed.");
        }
        return a * b / gcd(a, b);
    }

    /**
     * randomDivisible: return a "random" number divisible by given input.
     */
    private static int randomDivisible(int max, int divisor) {
        /*
         * Can generate a random number until one divisible is found.
         * May take too many attempts = max/divisor, in expectation.
         * Here, simplify. Choose a random number. Add or subtract enough
         * to make it divisible.
         */
        if (divisor > max) {
            throw new IllegalArgumentException("Divisor cannot be greater than limit.");
        }
        int temp = random.nextInt(max);
        if (temp % divisor != 0) {
            int difference = divisor - (temp % divisor);
            /*
             * Checking for a higher value means we don't need to worry
             * about returing 0.
             */
            if (temp + difference > max) {
                return temp - (temp % divisor);
            } else {
                return temp + difference;
            }
        }
        return temp;
    }

    /**
     * Create a set of random numbers (>= 0) that add up to sum.
     */
    public static int[] randomSet(int n, int sum) {
        if ((n < 1) || (sum < 0)) {
            throw new IllegalArgumentException("Cannot satisfy sum.");
        }

        int[] temp = new int[n];

        for (int i = 0; i < (n - 1); i++) {
            temp[i] = random.nextInt(sum);
        }

        temp[n - 1] = sum;
        Arrays.sort(temp);

        int prev = temp[n - 1];

        for (int i = n - 1; i > 0; i--) {
            temp[i] -= temp[i - 1];
        }

        if (n != 1) {
            temp[0] = (sum + temp[0] - prev) % sum;
        }

        return temp;
    }

    /**
     * Create a set of random numbers (>= 0) that add up to sum.
     */
    public static long[] randomSet(int n, long sum) {
        if ((n < 1) || (sum < 0)) {
            throw new IllegalArgumentException("Cannot satisfy sum");
        }

        long[] temp = new long[n];

        for (int i = 0; i < (n - 1); i++) {
            temp[i] = random.nextLong() % sum;
        }

        temp[n - 1] = sum;
        Arrays.sort(temp);

        long prev = temp[n - 1];

        for (int i = n - 1; i > 0; i--) {
            temp[i] -= temp[i - 1];
        }

        if (n != 1) {
            temp[0] = (sum + temp[0] - prev) % sum;
        }

        return temp;
    }

    /**
     * Create a set of random numbers (>=1) that add up to sum.
     * There may be a better way to do this with repeated calls to
     * randomSet(). Small values of sum may lead to more repeats?
     * Something like n^2/(2 * sum).
     */
    public static int[] nonZeroRandomSet(int n, int sum) {
        if (sum < n) {
            throw new IllegalArgumentException("Cannot satisfy sum.");
        }
        
        for (int i = 0; i < MAX_TRIES; i++) {
            boolean found = true;
            int[] temp = randomSet(n, sum);

            inner: for (int j = 0; j < n; j++) {
                if (temp[j] == 0) {
                    found = false;
                    break;
                }
            }
            
            if (found) {
                return temp;
            }
        }
        
        throw new RuntimeException("too many attempts. n, sum = " + n + ", " + sum);
    }

    public static int[] maxNonZeroRandomSet(int n, int sum, int max) {
        if (sum < n) {
            throw new IllegalArgumentException("Cannot satisfy sum.");
        }
        
        for (int i = 0; i < MAX_TRIES; i++) {
            boolean found = true;
            int[] temp = randomSet(n, sum);

            inner: for (int j = 0; j < n; j++) {
                if (temp[j] == 0 || temp[j] > max) {
                    found = false;
                    break;
                }
            }
            
            if (found) {
                return temp;
            }
        }
        
        throw new RuntimeException("too many attempts. n, sum, max = " + n + ", " + sum + ", " + max);
    }

    /**
     * Return a non-zero random number less than or equal to
     * max.
     */
    public static int nonZeroRandom(int max) {
        assert (max > 0);

        return 1 + random.nextInt(max - 1);
    }

    /**
     * Return non-zero random numbers that are close to the average = sum/n
     * and sum up to given value
     */
    public static int[] closeNonZeroRandoms(int n, int sum, double tolerance) {
        if (sum < n) {
            throw new RuntimeException("Impossible to satisfy: (n, sum) = " + n + "," + sum);
        }

        int[] temp = new int[n];

        if (n == 1) {
            temp[0] = sum;
            return temp;
        }

        boolean found = false;

        for (int t = 0; t < MAX_TRIES && !found; t++) {
            for (int i = 0; i < n - 1; i++) {
                temp[i] = (i + 1) * sum / n;
            }
            temp[n - 1] = sum;
            /*
             * Perturb the inner values within tolerance.
             */

            for (int i = 0; i < (n - 1); i++) {
                /*
                 * Get perturbation factor uniformly chosen between
                 * [-0.5 * tolerance, 0.5 * tolerance].
                 */
                double factor = (2.0 * random.nextDouble() - 1.0) * 0.5 * tolerance;

                temp[i] += (factor * (sum / n));
            }

            for (int i = n - 1; i > 0; i--) {
                temp[i] -= temp[i - 1];
            }

            found = true;
            for (int i = 0; i < n; i++) {
                if (temp[i] == 0) {
                    found = false;
                } else if (temp[i] < 0) {
                    throw new RuntimeException("Damn");
                }
            }
        }

        if (!found) {
            /*
             * We could throw an exception here. But it is usually OK to
             * just return the initial values.
             */

            for (int i = 0; i < n - 1; i++) {
                temp[i] = sum / n;
            }
            temp[n - 1] = sum - (n - 1) * (sum / n);

        }

        return temp;
    }

    /**
     * Return non-zero random numbers that are close to the average = sum/n
     * and sum up to given value.
     */
    public static long[] closeNonZeroRandoms(int n, long sum, double tolerance) {
        if (sum < n) {
            throw new RuntimeException("Impossible to satisfy: (n, sum) = " + n + "," + sum);
        }

        long[] temp = new long[n];

        if (n == 1) {
            temp[0] = sum;
            return temp;
        }

        boolean found = false;

        for (int t = 0; t < MAX_TRIES && !found; t++) {
            for (int i = 0; i < n - 1; i++) {
                temp[i] = (i + 1) * sum / n;
            }
            temp[n - 1] = sum;
            /*
             * Perturb the inner values within tolerance.
             */

            for (int i = 0; i < (n - 1); i++) {
                /*
                 * Get perturbation factor uniformly chosen between
                 * [-0.5 * tolerance, 0.5 * tolerance].
                 */
                double factor = (2.0 * random.nextDouble() - 1.0) * 0.5 * tolerance;

                temp[i] += (factor * sum / n);
            }

            for (int i = n - 1; i > 0; i--) {
                temp[i] -= temp[i - 1];
            }

            found = true;
            for (int i = 0; i < n; i++) {
                if (temp[i] == 0) {
                    found = false;
                }
            }
        }
        if (!found) {
            /*
             * We could throw an exception here. But it is usually OK to
             * just return the initial values.
             */

            for (int i = 0; i < n - 1; i++) {
                temp[i] = sum / n;
            }
            temp[n - 1] = sum - (n - 1) * (sum / n);
        }

        return temp;
    }

    public static double truncatedNormal(double mean, double variance) {
        return truncatedNormal(mean, variance, 0.5);
    }

    /**
     * Generated a random variable from a truncated normal distribution.
     * Tolerance is defined in terms of mean.
     */
    private static double truncatedNormal(double mean, double variance, double tolerance) {
        if (variance < 0) {
            throw new IllegalArgumentException("Variance cannot be less than 0: " + variance);
        }

        double temp;
        double stddev = Math.sqrt(variance);

        /*
         * Try a limited (100) number of times to generate a suitable rv. If
         * this fails, return mean.
         */
        for (int i = 0; i < 100; i++) {
            temp = random.nextGaussian();
            temp = temp * stddev + mean;
            if (Math.abs(mean - temp) <= tolerance * mean) {
                return temp;
            }
        }

        return mean;
    }

    /**
     * Generate a uniform random integer in the specified interval (start, end].
     */
    public static int randomInt(int start, int end) {
        if (start == end) {
            return start;
        } else {
            return start + random.nextInt(end - start);
        }
    }
    
    public static int randomInt(int mean, double tolerance) {
        return randomInt((int) (Math.ceil(mean * (1.0 - tolerance))),
                (int) (Math.floor(mean * (1.0 + tolerance))));
    }
    
    /**
     * Generate a uniform random integer in the specified interval (start, end].
     */
    private static long randomLong(long start, long end) {
        if (start == end) {
            return start;
        } else {
            return start + random.nextLong() % (end - start);
        }
    }
    
    public static long randomLong(long mean, double tolerance) {
        return randomLong((long) (Math.ceil(mean * (1.0 - tolerance))),
                (long) (Math.floor(mean * (1.0 + tolerance))));
    }
    
    public static boolean randomToss(double bias) {
        return random.nextDouble() <= bias;
    }
    
    public static double randomDouble(double min, double max) {
        if (min == max) {
            return min;
        } else {
            return min + random.nextDouble() * (max - min);
        }
    }
    
    /**
     * Reverse array from start (inclusive) to end (exclusive).
     */
    public static void reverse(int[] array, int start, int end) {
        int length = end - start;

        for (int i = 0; i < (length / 2); i++) {
            int temp = array[start + i];
            array[start + i] = array[end - 1 - i];
            array[end - 1 - i] = temp;
        }
    }

    public static void reverse(int[] array) {
        reverse(array, 0, array.length);
    }

    public static void print(int[] array) {
        for (int anArray : array) {
            System.out.print(anArray + " ");
        }

        System.out.println();
    }

    public static void testRandomSet(int n, int sum) {
        int[] test = randomSet(n, sum);
        int gensum = 0;

        for (int i = 0; i < n; i++) {
            System.out.print(test[i] + " ");
            gensum += test[i];
        }

        System.out.println("= " + gensum);
    }

    public static void testNonZeroRandomSet(int n, int sum) {
        int[] test = nonZeroRandomSet(n, sum);
        int gensum = 0;

        for (int i = 0; i < n; i++) {
            System.out.print(test[i] + " ");
            gensum += test[i];
        }

        System.out.println("= " + gensum);
    }

    public static void testCloseNonZeroRandoms(int n, long sum, double tolerance) {
        long[] test = closeNonZeroRandoms(n, sum, tolerance);
        long gensum = 0;

        for (int i = 0; i < n; i++) {
            System.out.print(test[i] + " ");
            gensum += test[i];
        }

        System.out.println("= " + gensum);
    }

    private static void testCloseNonZeroRandoms(int n, int sum, double tolerance) {
        int[] test = closeNonZeroRandoms(n, sum, tolerance);
        int gensum = 0;

        for (int i = 0; i < n; i++) {
            System.out.print(test[i] + " ");
            gensum += test[i];
        }

        System.out.println("= " + gensum);
    }

    public static void testRandomDivisible(int max, int divisor) {
        System.out.println(max + " " + divisor + " " + randomDivisible(max, divisor));
    }

    public static void main(String[] args) {
        //testNonZeroRandomSet(31, 245);
        //testNonZeroRandomSet(2, 6);
        //testNonZeroRandomSet(2, 6);
        //testNonZeroRandomSet(3, 10);
        testCloseNonZeroRandoms(5, 7, 0.25);
        //testCloseNonZeroRandoms(2, 50, 0.25);
        //testRandomDivisible(50, 7);
        //testRandomDivisible(1000, 26);
        //testRandomSet(10, 1000);
        //testRandomSet(3, 10);
    }
}



