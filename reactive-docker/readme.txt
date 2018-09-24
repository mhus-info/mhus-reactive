====
    Copyright 2018 Mike Hummel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
====


cd reactive-playground
./create.sh


docker tag reactive-playground "mhus/reactive-playground:1.0.0"
docker tag reactive-playground "mhus/reactive-playground:latest"

docker push "mhus/reactive-playground:1.0.0"
docker push "mhus/reactive-playground:latest"

First Start:
docker rm reactive-playground
docker run -it --name reactive-playground -p 18181:8181 reactive-playground

Go to http://localhost:18181/sop

Again:
docker start -i reactive-playground
