#!/bin/bash

mvn package docker:build -Dmaven.test.skip=true
docker run -p 12345:12345 -e ASEPART_POSTGRES_HOST='asepart-postgres' --network asepart-network --name asepart-webservice -d asepart-webservice 

exit 0

