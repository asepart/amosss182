#!/bin/sh

docker stop asepart-webservice
docker rm asepart-webservice
docker rmi asepart-webservice

rm -rf target
