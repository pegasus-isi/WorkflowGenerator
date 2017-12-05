/*******************************************************************************
 * In the Hi-WAY project we propose a novel approach of executing scientific
 * workflows processing Big Data, as found in NGS applications, on distributed
 * computational infrastructures. The Hi-WAY software stack comprises the func-
 * tional workflow language Cuneiform as well as the Hi-WAY ApplicationMaster
 * for Apache Hadoop 2.x (YARN).
 *
 * List of Contributors:
 *
 * Marc Bux (HU Berlin)
 * Jörgen Brandt (HU Berlin)
 * Hannes Schuh (HU Berlin)
 * Carl Witt (HU Berlin)
 * Ulf Leser (HU Berlin)
 *
 * Jörgen Brandt is funded by the European Commission through the BiobankCloud
 * project. Marc Bux is funded by the Deutsche Forschungsgemeinschaft through
 * research training group SOAMED (GRK 1651).
 *
 * Copyright 2014 Humboldt-Universität zu Berlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package simulation.generator.util;

import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import java.util.Random;

/**
 * Created by Carl Witt on 13.11.17.
 * A relationship between a task's read amount of data (sum of input file sizes) and peak memory consumption.
 * We start from a linear model on input size and add unexplained (normally distributed) variation.
 *
 * peak mem = slope * input size + intercept + random value sampled from Normal(0, variance)
 */
public class MemoryModel{

    /** The slope of the linear function. */
    final double slope;
    /** The axis intercept of the linear function. */
    final double intercept;
    /** The random number generator used to generate the unexplained variation of the peak memory consumption (as opposed to the explained variation by the input file size). */
    Random error;
    /** The standard deviation of the error that is added to the linear model.*/
    double errorStandardDeviation;


    /**
     * peak mem will be sampled from slope * input size + intercept + random value in range [-err, +err]
     * @param slope The slope of the linear function.
     * @param intercept The axis intercept of the linear function.
     * @param errorStandardDeviation The standard deviation of the (zero mean) error added to the linear model.
     */
    public MemoryModel(double slope, double intercept, double errorStandardDeviation) {
        this.slope = slope;
        this.intercept = intercept;
        this.errorStandardDeviation = errorStandardDeviation;
        error = new Random();
    }

    public static MemoryModel constant(double value, double errorStandardDeviation){
        return new MemoryModel(0, value, errorStandardDeviation);
    }

    /**
     * peak mem = slope * input size + intercept + random value sampled from Normal(0, variance)
     * A random value following a linear model with some normally distributed error.
     * @param inputFileSize The size of the input file, usually in bytes.
     * @return a random value ≥ 1 according to the model.
     */
    public long getPeakMemoryConsumption(long inputFileSize){
        return Math.max(1, (long) (inputFileSize*slope + intercept + error.nextGaussian()*errorStandardDeviation));
    }


}