
== Fast Start ....



version=1.1.0-SNAPSHOT
feature:repo-add mvn:de.mhus.cherry.reactive/reactive-feature/$version/xml/features

feature:install cherry-reactive-dev cherry-reactive-all







OLD: 

- admin.txt
- bpm.txt
- mhuslib config in etc
- api-* in deploy
- Testumgebung mhu lib
- Prodktiv mit mysql: datasource in deploy


Download karaf 4.0.10 from karaf.apache.org start it and install the engine:

version=1.3.4
feature:repo-add activemq 5.12.1
feature:repo-add cxf 3.1.5
feature:repo-add mvn:de.mhus.osgi/mhus-features/$version/xml/features

feature:install mhu-osgi-base

version=1.0.0-SNAPSHOT
install -s mvn:org.hsqldb/hsqldb/2.3.2
install -s mvn:de.mhus.cherry.reactive/reactive-osgi/$version
install -s mvn:de.mhus.cherry.reactive/reactive-karaf/$version

install -s mvn:de.mhus.cherry.reactive/reactive-dev/$version

install -s mvn:de.mhus.cherry.reactive/reactive-test/$version

Rest:

install -s mvn:de.mhus.osgi/mhu-sop-api/1.3.2-SNAPSHOT
install -s mvn:de.mhus.osgi/mhu-sop-core/1.3.2-SNAPSHOT
install -s mvn:de.mhus.osgi/mhu-sop-rest/1.3.2-SNAPSHOT

install -s mvn:de.mhus.cherry.reactive/reactive-sop-rest/$version



pls -a

pdeploy -a de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1

pstart 'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=1234;customerId=alf?text1=second'
pstart 'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=1234;customerId=alf?text1=third'
pstart 'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=1234;customerId=alf?text1=error1'
pstart 'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=1234;customerId=alf?text1=parallel1'
pstart 'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=1234;customerId=alf?text1=parallel2'

Super Stress:

pstress -i 1 \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=second' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=third' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=error1' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel1' \
'bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel2'




== GUI

feature:install mhu-osgi-vaadin

install -s mvn:de.mhus.ports/vaadin-refresher/1.3.4
install -s mvn:de.mhus.osgi/mhu-sop-vaadin-theme/1.3.2-SNAPSHOT
install -s mvn:de.mhus.osgi/mhu-sop-vaadin-desktop/1.3.2-SNAPSHOT

install -s mvn:de.mhus.cherry.reactive/reactive-vaadin-widgets/$version
install -s mvn:de.mhus.cherry.reactive/reactive-vaadin-core/$version


install -s mvn:de.mhus.cherry.reactive/cr-example-user-management/$version

 
