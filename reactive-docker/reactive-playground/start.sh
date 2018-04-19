#!/bin/bash

. /root/.m2/reactive_env.sh
cd /opt/karaf

while [ "x$START_WAIT" == "x1" ]; do
  echo "START_WAIT is set ... waiting"
  sleep 10
  . /root/.m2/reactive_env.sh
done

# Start Karaf

./bin/karaf
