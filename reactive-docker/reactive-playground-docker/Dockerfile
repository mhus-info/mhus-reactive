FROM mhus/apache-karaf:4.2.6_04
MAINTAINER Mike Hummel <mh@mhus.de>

#ENV KARAF_UID 0
ENV JAVA_MAX_MEM 2048m

COPY target/assembly.tar.gz /docker/assembly.tar.gz
COPY profiles /docker/profiles

# Start once - prepare for a faster start
RUN /docker/assembly.sh
RUN export DEPLOY_FORCE=1 && /docker/install.sh
