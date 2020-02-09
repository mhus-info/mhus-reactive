#!/bin/bash

mvn install

cd reactive-playground-docker
./create.sh $@
cd ..

