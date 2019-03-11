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


docker tag reactive-playground "mhus/reactive-playground:1.6.0-SNAPSHOT"
docker tag reactive-playground "mhus/reactive-playground:latest"

docker push "mhus/reactive-playground:1.6.0-SNAPSHOT"
docker push "mhus/reactive-playground:latest"

First Start:

* docker rm reactive-playground

* docker run -it --name reactive-playground -p 18181:8181 mhus/reactive-playground:1.0.2.2-SNAPSHOT

* Go to http://localhost:18181/sop

* Log with admin - 123

* Open in Menu Engine -> Execute

* Insert: bpm://de.mhus.cherry.reactive.examples.simple1.S1Process/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=form01

* Submit form

* Refresh 'unassigned' by pressing the 'magnifying glass'. You should see a new task.

* Select the new task press right mouse button and choose 'do it'.

* Fill The form and submit the form.

* Refresh 'unassigned' by pressing the 'magnifying glass'. You should see no task.

-----------------
Misc:

Start again:
docker start -i reactive-playground

Start with shared maven repository (for development):
docker run -it --name reactive-playground -v /home/user/.m2:/root/.m2 -p 18181:8181 mhus/reactive-playground

Commit and Push:
docker commit reactive-playground mhus/reactive-playground:1.0.2.3-SNAPSHOT
sha256:275f54714734a6583c2c0420688bed957fa6289dd3c516bfce11f2fe8b883a40
docker tag 275f54714734a6583c2c0420688bed957fa6289dd3c516bfce11f2fe8b883a40 mhus/reactive-playground:1.0.2.3-SNAPSHOT

