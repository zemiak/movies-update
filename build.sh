#!/bin/sh

cd ./src/main/docker || exit 10
docker build . -f Dockerfile -t movies-moviethumbnails
if [ $? -ne 0 ]
then
    exit 15
fi

cd ../../.. || exit 17
