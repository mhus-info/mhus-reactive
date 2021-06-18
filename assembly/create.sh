#!/bin/bash
#
# Copyright (C) 2018 Mike Hummel (mh@mhus.de)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


VERSION=7.5.0.2
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
