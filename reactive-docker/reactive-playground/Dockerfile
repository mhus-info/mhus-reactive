FROM mhus/apache-karaf-playground:4.0.10
MAINTAINER Mike Hummel <mh@mhus.de>

#ENV KARAF_UID 0
ENV JAVA_MAX_MEM 2048m
ENV REACTIVE_VERSION 1.0.2-SNAPSHOT

# copy maven repo stuff
COPY repository /root/.m2/repository/

# Install karaf feature
RUN set -x \
	&& find ~/.m2/repository \
	&& cd /opt/karaf \
	&& ./bin/start \
	&& sleep 2 \
	&& (tail -f /opt/karaf/data/log/karaf.log &) \
	&& echo "Wait for startup" \
	&& while [ "$(grep -c Done data/log/karaf.log)" = "0" ]; do echo "."; sleep 10; done \
	&& sleep 5 \
	&& echo "feature:repo-add mvn:de.mhus.cherry.reactive/reactive-feature/${REACTIVE_VERSION}/xml/features" | ./bin/client \
	&& sleep 2 \
	&& echo "feature:install cherry-reactive-dev cherry-reactive-all" | ./bin/client \
	&& sleep 2 \
	&& ./bin/stop \
	&& echo "Wait for shutdown" \
	&& sleep 5 \
	&& while [ "$(grep -c Stopping\ JMX\ OSGi\ agent data/log/karaf.log)" = "0" ]; do echo "."; sleep 5; done \
	&& rm -r ~/.m2/repository/* \
	&& rm -r /opt/karaf/data/log/*
	
COPY etc/* /opt/karaf/etc/
COPY deploy/* /opt/karaf/deploy/
COPY account/* /opt/karaf/sop/aaa/account/
#COPY trust/* /opt/karaf/sop/aaa/trust/
COPY groupmapping/* /opt/karaf/sop/aaa/groupmapping/

