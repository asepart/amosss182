#!/bin/sh

docker stop asepart-postgres
docker rm asepart-postgres
docker network rm asepart-network

exit 0

