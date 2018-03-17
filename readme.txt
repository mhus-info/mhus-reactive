
version=1.3.3-SNAPSHOT
feature:repo-add activemq 5.12.1
feature:repo-add cxf 3.1.5
feature:repo-add mvn:de.mhus.osgi/mhu-karaf-feature/$version/xml/features

feature:install mhu-osgi-base

version=1.0.0-SNAPSHOT
install -s mvn:de.mhus.cherry.reactive/reactive-osgi/$version
install -s mvn:de.mhus.cherry.reactive/reactive-karaf/$version

install -s mvn:de.mhus.cherry.reactive/reactive-examples/$version

pls -a
