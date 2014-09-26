#!/bin/bash

export DIR=$(cd $(dirname $0)/.. && pwd)

CLASSPATH=$DIR/classes
for jar in $(ls $DIR/lib/*.jar); do
    CLASSPATH=$CLASSPATH:$jar
done

export CLASSPATH

