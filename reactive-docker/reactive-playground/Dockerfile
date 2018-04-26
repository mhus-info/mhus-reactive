FROM mhus/apache-karaf:4.0.10
MAINTAINER Mike Hummel <mh@mhus.de>

#ENV KARAF_UID 0
ENV JAVA_MAX_MEM 2048m
ENV REACTIVE_VERSION 1.0.0

# copy maven repo stuff
COPY repository /root/.m2/repository/

# Install karaf feature
RUN find ~/.m2/repository \
	&& cd /opt/karaf \
	&& ./bin/start \
	&& sleep 2 \
	&& while [ "$(grep -c Done data/log/karaf.log)" = "0" ]; do echo "."; sleep 5; done \
	&& echo "feature:repo-add mvn:de.mhus.cherry.reactive/reactive-feature/${REACTIVE_VERSION}/xml/features" | ./bin/client \
	&& echo "feature:install cherry-reactive-dev cherry-reactive-all" | ./bin/client \
	&& sleep 2 \
	&& ./bin/stop \
	&& sleep 5 \
	&& while [ "$(grep -c Stopping\ JMX\ OSGi\ agent data/log/karaf.log)" = "0" ]; do echo "."; sleep 5; done \
	&& rm -r ~/.m2/repository/*
	
COPY etc/* /opt/karaf/etc/
COPY deploy/* /opt/karaf/deploy/
COPY account/* /opt/karaf/sop/aaa/account/
#COPY trust/* /opt/karaf/sop/aaa/trust/
COPY groupmapping/* /opt/karaf/sop/aaa/groupmapping/

