FROM mhus/apache-karaf:4.2.6_03
MAINTAINER Mike Hummel <mh@mhus.de>

#ENV KARAF_UID 0
ENV JAVA_MAX_MEM 2048m

COPY target/assembly.tar.gz /docker/assembly.tar.gz
COPY profiles /docker/profiles

# Start once
RUN export CONFIG_PROFILE=none && /docker/environment.sh
RUN export INSTALL_FORCE=1 && /docker/install.sh
RUN rm /opt/karaf/environmentdone.mark
