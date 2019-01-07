FROM mhus/apache-karaf-playground:4.2.1
MAINTAINER Mike Hummel <mh@mhus.de>

#ENV KARAF_UID 0
ENV JAVA_MAX_MEM 2048m

# copy maven repo stuff
COPY repository /root/.m2/repository/
COPY install.gogo /

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
	&& cat /install.gogo | ./bin/client \
	&& sleep 2 \
	&& ./bin/stop \
	&& echo "Wait for shutdown" \
	&& sleep 5 \
	&& while [ "$(grep -c Stopping\ JMX\ OSGi\ agent data/log/karaf.log)" = "0" ]; do echo "."; sleep 5; done \
	&& rm -r ~/.m2/repository/* \
	&& rm -r /opt/karaf/data/log/*
	
COPY karaf/* /opt/karaf/

