#!/bin/bash

mvn install || exit 1

cd reactive-playground-docker
./create.sh $@ || exit 1
cd ..

if [ "$1" = "test" ]; then
    shift
    docker stop reactive-playground
    docker rm reactive-playground

    docker run -it --name reactive-playground \
     -h reactive \
     -v ~/.m2:/home/user/.m2 \
     -p 8181:8181 \
     -p 15005:5005 \
     mhus/reactive-playground:7.0.0-SNAPSHOT debug
fi

if [ "$1" = "k8s" ]; then
    shift
    kubectl apply -f kubernetes/reactive-test.yaml
fi
