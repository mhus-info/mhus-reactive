#!/bin/bash
#
# Copyright 2018 Mike Hummel
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cd /opt/karaf
echo "====================================="
echo "Install Features"
echo "====================================="
rm -r data/cache/*
rm data/log/*
echo "-------------------------------------"
echo "Start karaf in background"
echo "-------------------------------------"
./bin/start
sleep 5
(tail -f data/log/karaf.log) &
while [ "$(grep -c Done data/log/karaf.log)" = "0" ]; do
  echo "."
  sleep 5
done

cnt=1

while [ -e /deploy${cnt}.gogo ]
do
    echo "-------------------------------------"
    echo "Installation of /deploy${cnt}.gogo"
    echo "-------------------------------------"
    cat /deploy${cnt}.gogo
    echo "-------------------------------------"
    cat /deploy${cnt}.gogo | ./bin/client
    echo "-------------------------------------"
    echo "DEPLOY FINISHED"
    echo "-------------------------------------"
    
#    for c in 1 2 3 4 5 6 7 8 9 10
#    do
#        echo Round $c
#        echo "-------------------------------------"
#        echo list | ./bin/client
#        echo "-------------------------------------"
#        sleep 1
#    done
    sleep 10
    
    echo "-------------------------------------"
    echo "Actual Bundles"
    echo "-------------------------------------"
    echo list | ./bin/client

    let cnt=${cnt}+1
done

echo "-------------------------------------"
echo "Stop karaf"
echo "-------------------------------------"
./bin/stop
sleep 5
while [ "$(grep -c Stopping\ JMX\ OSGi\ agent data/log/karaf.log)" = "0" ]; do
  echo "."
  sleep 5
done
touch installdone.mark
killall tail
echo "====================================="
echo "Finish"
echo "====================================="
