FROM mhus/apache-karaf-playground:4.2.2
MAINTAINER Mike Hummel <mh@mhus.de>

#ENV KARAF_UID 0
ENV JAVA_MAX_MEM 2048m

# copy maven repo stuff
COPY repository /root/.m2/repository/

COPY org.apache.karaf.features.cfg /opt/karaf/etc/

# Install karaf feature
RUN set -x \
    && find ~/.m2/repository \
    && cd /opt/karaf \
    && ./bin/start \
    && sleep 2 \
    && (tail -f /opt/karaf/data/log/karaf.log &) \
    && echo "Wait for startup 1" \
    && while [ "$(grep -c Done data/log/karaf.log)" = "0" ]; do echo "."; sleep 10; done \
    && sleep 5 \
    && ./bin/stop \
    && echo "Wait for shutdown 1" \
    && sleep 5 \
    && while [ "$(ps -efa | grep -v grep | grep -c java)" != "0" ]; do echo "."; sleep 5; done \
    && rm -r /opt/karaf/data/log/* \
    && ./bin/start \
    && sleep 2 \
    && (tail -f /opt/karaf/data/log/karaf.log &) \
    && echo "Wait for startup 2" \
    && while [ "$(grep -c Done data/log/karaf.log)" = "0" ]; do echo "."; sleep 10; done \
    && sleep 5 \
    && ./bin/stop \
    && echo "Wait for shutdown 2" \
    && sleep 5 \
    && while [ "$(ps -efa | grep -v grep | grep -c java)" != "0" ]; do echo "."; sleep 5; done \
    && rm -r ~/.m2/repository/* \
    && touch /opt/karaf/installdone.mark \
    && rm -r /opt/karaf/data/log/* 
    
	
COPY karaf /opt/karaf/

