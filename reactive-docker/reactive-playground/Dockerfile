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
    && ./install.sh

COPY karaf /opt/karaf/

