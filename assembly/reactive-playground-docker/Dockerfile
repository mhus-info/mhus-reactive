FROM mhus/karaf-k8s:4.2.6.0
LABEL AUTHOR Mike Hummel <mh@mhus.de>

COPY target/assembly.tar.gz /docker/assembly.tar.gz
COPY profiles /docker/profiles
COPY profiles/default /docker/profiles/custom-initial
RUN /docker/install.sh
