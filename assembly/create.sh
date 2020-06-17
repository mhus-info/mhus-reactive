#!/bin/bash

VERSION=7.2.0
REPOSITORY=mhus/reactive-playground

mvn install -P assembly || exit 1

cd reactive-playground-docker

if [  ! -f Dockerfile ]; then
  echo "not a docker configuration"
  return 1
fi

if [ "$1" = "clean" ]; then
    shift
    docker rmi $REPOSITORY:$VERSION
    docker build --no-cache -t $REPOSITORY:$VERSION .
else
    docker build -t $REPOSITORY:$VERSION .
fi

if [ "$1" = "push" ]; then
    docker push "$REPOSITORY:$VERSION"
fi

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
     mhus/reactive-playground:$VERSION debug
fi

if [ "$1" = "k8s" ]; then
    shift
    kubectl apply -f kubernetes/reactive-test.yaml
fi
