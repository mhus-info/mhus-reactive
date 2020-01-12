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

Create docker image:

mvn dockerfile:build


Publish docker image:

docker push "mhus/reactive-playground:7.0.0-SNAPSHOT"

---

Create Docker playground as single node with in memory db:

docker run -it --name reactive-playground \
 -h reactive \
 -v ~/.m2:/home/user/.m2 \
 -p 8181:8181 \
 -p 15005:5005 \
 mhus/reactive-playground:7.0.0-SNAPSHOT debug

docker rm reactive-playground

---

Execute sample prrocess:

http://localhost:8181/sop

Execute:

bpm://de.mhus.cherry.reactive.examples.simple1.S1Process/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=form01

pstart "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=test;customerId=alf?text1=second"


Additional console:

docker exec -it reactive-playground /opt/karaf/bin/client

---

Create single with mysql:


docker run -d --name reactive-db \
 -e MYSQL_ROOT_PASSWORD=nein \
 mariadb:10.3

docker exec -it reactive-db mysql -pnein

CREATE DATABASE db_bpm_stor;
CREATE DATABASE db_bpm_arch;
CREATE OR REPLACE USER 'db_bpm_arch'@'%' IDENTIFIED BY 'nein';
CREATE OR REPLACE USER 'db_bpm_stor'@'%' IDENTIFIED BY 'nein';
GRANT ALL PRIVILEGES ON db_bpm_arch.* TO 'db_bpm_arch'@'%';
GRANT ALL PRIVILEGES ON db_bpm_stor.* TO 'db_bpm_stor'@'%';
quit


docker run -it --name reactive-playground \
 -h reactive \
 -p 8181:8181 \
 -p 15005:5005 \
 --link reactive-db:dbserver \
 -v ~/.m2:/home/user/.m2 \
 -e CONFIG_PROFILE=single \
 -e ENV_DB_BPM_PASS=nein \
 -e ENV_JMS_SOP_USER=admin \
 -e ENV_JMS_SOP_PASS=nein \
 --oom-kill-disable \
 mhus/reactive-playground:7.0.0-SNAPSHOT debug

deploy process:

pdeploy -a de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1

---

Create multi node environment with mysql:

Create mysql from single node and ...


docker run --name='reactive-jms' -d \
 -e 'ACTIVEMQ_CONFIG_NAME=amqp-srv1' \
 -e 'ACTIVEMQ_CONFIG_DEFAULTACCOUNT=false' \
 -e 'ACTIVEMQ_ADMIN_LOGIN=admin' -e 'ACTIVEMQ_ADMIN_PASSWORD=nein' \
 -e 'ACTIVEMQ_CONFIG_MINMEMORY=1024' -e  'ACTIVEMQ_CONFIG_MAXMEMORY=4096' \
 -e 'ACTIVEMQ_CONFIG_SCHEDULERENABLED=true' \
 webcenter/activemq:5.14.3


docker run -it --name reactive-playground1 \
 -h reactive1 \
 -p 8181:8181 \
 -p 15005:5005 \
 --link reactive-db:dbserver \
 --link reactive-jms:jmsserver \
 -v ~/.m2:/home/user/.m2 \
 -e CONFIG_PROFILE=multi \
 -e ENV_DB_BPM_PASS=nein \
 -e ENV_JMS_SOP_USER=admin \
 -e ENV_JMS_SOP_PASS=nein \
 --oom-kill-disable \
 mhus/reactive-playground:7.0.0-SNAPSHOT debug

docker run -it --name reactive-playground2 \
 -h reactive2 \
 -p 8182:8181 \
 -p 15006:5005 \
 --link reactive-db:dbserver \
 --link reactive-jms:jmsserver \
 -v ~/.m2:/home/user/.m2 \
 -e CONFIG_PROFILE=multi \
 -e ENV_DB_BPM_PASS=nein \
 -e ENV_JMS_SOP_USER=admin \
 -e ENV_JMS_SOP_PASS=nein \
 --oom-kill-disable \
 mhus/reactive-playground:7.0.0-SNAPSHOT debug

docker stop reactive-playground1 
docker stop reactive-playground2
docker rm reactive-playground1 
docker rm reactive-playground2

---

Start stress tool:

pstress -i 1 -m 50 \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=second' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=third' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=error1' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel1' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel2'

---

Enable case logging:

mhus:config set de.mhus.cherry.reactive.osgi.ReactiveAdmin logCases true
 
 

