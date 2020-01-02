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

mvn dockerfile:build

docker push "mhus/reactive-playground:1.6.3-SNAPSHOT"


docker run -it --name reactive-playground -h reactive -v ~/.m2:/home/user/.m2 -p 8181:8181 mhus/reactive-playground:1.6.3-SNAPSHOT

docker rm reactive-playground


bpm://de.mhus.cherry.reactive.examples.simple1.S1Process/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=form01

---

Multiple nodes:

docker run --name reactive-db -e MYSQL_ROOT_PASSWORD=nein -d mariadb:10.3

docker exec -it reactive-db mysql -pnein

CREATE DATABASE db_bpm_stor;
CREATE DATABASE db_bpm_arch;
CREATE OR REPLACE USER 'db_bpm_arch'@'%' IDENTIFIED BY 'nein';
CREATE OR REPLACE USER 'db_bpm_stor'@'%' IDENTIFIED BY 'nein';
GRANT ALL PRIVILEGES ON db_bpm_arch.* TO 'db_bpm_arch'@'%';
GRANT ALL PRIVILEGES ON db_bpm_stor.* TO 'db_bpm_stor'@'%';
quit

docker run --name='reactive-jms' -d \
 -e 'ACTIVEMQ_CONFIG_NAME=amqp-srv1' \
 -e 'ACTIVEMQ_CONFIG_DEFAULTACCOUNT=false' \
 -e 'ACTIVEMQ_ADMIN_LOGIN=admin' -e 'ACTIVEMQ_ADMIN_PASSWORD=nein' \
 -e 'ACTIVEMQ_CONFIG_MINMEMORY=1024' -e  'ACTIVEMQ_CONFIG_MAXMEMORY=4096' \
 -e 'ACTIVEMQ_CONFIG_SCHEDULERENABLED=true' \
 webcenter/activemq:5.14.3

docker run -it --name reactive-playground1 -h reactive1 -p 8181:8181 --link reactive-db:dbserver --link reactive-jms:jmsserver -v ~/.m2:/home/user/.m2 -e CONFIG_PROFILE=sop -e ENV_DB_BPM_PASS=nein -e ENV_JMS_SOP_USER=admin -e ENV_JMS_SOP_PASS=nein mhus/reactive-playground:7.0.0-SNAPSHOT

docker run -it --name reactive-playground2 -h reactive2 -p 8182:8181 --link reactive-db:dbserver --link reactive-jms:jmsserver -v ~/.m2:/home/user/.m2 -e CONFIG_PROFILE=sop -e ENV_DB_BPM_PASS=nein -e ENV_JMS_SOP_USER=admin -e ENV_JMS_SOP_PASS=nein mhus/reactive-playground:7.0.0-SNAPSHOT

docker stop reactive-playground1 
docker stop reactive-playground2
docker rm reactive-playground1 
docker rm reactive-playground2

pstress -i 1 \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=second' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=third' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=error1' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel1' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel2'



