#!/bin/sh

mvn package -q && docker build . -t movies-update:latest -f ./src/main/docker/Dockerfile.fast-jar -q
