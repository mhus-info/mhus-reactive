#!/bin/bash

rm -r repository/*
mkdir -p repository/de/mhus
cp -r ~/.m2/repository/de/mhus/* repository/de/mhus/

if [ "$1" = "clean" ]; then
	docker build --no-cache -t reactive-playground .
else
	docker build -t reactive-playground .
fi	
