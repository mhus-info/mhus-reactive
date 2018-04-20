FROM committed/java
MAINTAINER Mike Hummel <mh@mhus.de>

# Install the Java JCE Policy
RUN curl -q -L -C - -b "oraclelicense=accept-securebackup-cookie" -o /tmp/jce_policy-8.zip -O http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip \
    && unzip -oj -d /usr/lib/jvm/java-8-oracle/jre/lib/security /tmp/jce_policy-8.zip \*/\*.jar \
    && rm /tmp/jce_policy-8.zip

#ENV KARAF_UID 0
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
ENV KARAF_VERSION 4.0.10
ENV JAVA_MAX_MEM 2048m
ENV KARAF_EXEC exec
ENV REACTIVE_VERSION 1.0.0-SNAPSHOT

COPY download.sh /tmp/download.sh

RUN apt-get update && apt-get install -y --no-install-recommends jq curl vim less && rm -rf /var/lib/apt/lists/* \
    && chmod a+x /tmp/download.sh && sync && /tmp/download.sh && rm /tmp/download.sh \
    && mkdir -p /opt/karaf \
    && tar --strip-components=1 -C /opt/karaf -xzf /tmp/apache-karaf.tar.gz \
    && rm /tmp/apache-karaf.tar.gz \
    && mkdir -p /opt/karaf/data /opt/karaf/data/log \
    && mkdir -p /opt/karaf/sop/aaa/account /opt/karaf/sop/aaa/trust /opt/karaf/sop/aaa/groupmapping \
    && mkdir -p /root/.m2/repository \
    && sed -i 's/log4j\.rootLogger=INFO,\ out,\ osgi\:\*/log4j\.rootLogger=INFO,\ out,\ stdout,\ osgi\:\*/' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && rm -rf /var/lib/apt/lists/*

# copy maven repo stuff
COPY repository /root/.m2/repository/

# Install karaf feature
RUN find ~/.m2/repository \
	&& cd /opt/karaf \
	&& ./bin/start \
	&& sleep 2 \
	&& while [ "$(grep -c Done data/log/karaf.log)" = "0" ]; do echo "."; sleep 5; done \
	&& echo "feature:repo-add mvn:de.mhus.cherry.reactive/reactive-feature/${REACTIVE_VERSION}/xml/features\nfeature:install cherry-reactive-dev cherry-reactive-all\nsleep 1000\nlogout" | ./bin/client \
	&& sleep 2 \
	&& ./bin/stop \
	&& sleep 5 \
	&& while [ "$(grep -c Stopping\ JMX\ OSGi\ agent data/log/karaf.log)" = "0" ]; do echo "."; sleep 5; done
	
# Cleanup
RUN rm -r ~/.m2/repository/*

COPY etc/* /opt/karaf/etc/
COPY deploy/* /opt/karaf/deploy/
COPY account/* /opt/karaf/sop/aaa/account/
#COPY trust/* /opt/karaf/sop/aaa/trust/
COPY groupmapping/* /opt/karaf/sop/aaa/groupmapping/

COPY start.sh /start.sh
RUN chmod u+x /start.sh

EXPOSE 8181 18181

ENTRYPOINT ["/start.sh"]

