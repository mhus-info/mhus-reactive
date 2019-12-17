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


docker push "mhus/reactive-playground:1.6.3-SNAPSHOT"


docker run -it --name reactive-playground -h reactive -v ~/.m2:/home/user/.m2 -p 8181:8181 mhus/reactive-playground:1.6.3-SNAPSHOT

docker rm reactive-playground


bpm://de.mhus.cherry.reactive.examples.simple1.S1Process/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=form01
