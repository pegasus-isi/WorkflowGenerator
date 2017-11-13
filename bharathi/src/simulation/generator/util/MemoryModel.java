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

import java.util.Random;

/**
 * Created by Carl Witt on 13.11.17.
 * A relationship between a task's read amount of data (sum of input file sizes) and peak memory consumption.
 * We assume a deterministic behavior of memory consumption. Since we don't know a file's contents,
 * we assume two files of same size (which should be very rare for non-identical files) cause equal memory consumption.
 * We start from a linear model on input size, but we still want some unexplained (deterministic) variation, for which
 * we use a pseudo random number generator that is seeded with the file size.
 */
public class MemoryModel{
    final double slope;
    final double intercept;
    final double err;

    public MemoryModel(double slope, double intercept, double err) {
        this.slope = slope;
        this.intercept = intercept;
        this.err = err;
    }
    public static MemoryModel constant(double value, double err){
        return new MemoryModel(0, value, err);
    }

    public long getPeakMemoryConsumption(long inputFileSize){
        Random errorGenerator = new Random(inputFileSize);
        int signum = errorGenerator.nextInt(3)-1;
        return (long) (inputFileSize*slope + intercept + signum*errorGenerator.nextLong()%(long)err);
    }


}