#!/bin/sh

docker network create asepart-network

docker pull postgres
docker run -e POSTGRES_PASSWORD=asepart -p 5432:5432 --network asepart-network --name asepart-postgres -d postgres 

echo "Waiting for postgres startup..."
sleep 60 # TODO find better way to do this

docker exec asepart-postgres psql -c "create extension pgcrypto;" -U postgres
docker exec -i asepart-postgres psql -U postgres < schema.sql
docker exec -i asepart-postgres psql -U postgres < testdata.sql
docker exec asepart-postgres psql -c "alter user postgres with encrypted password 'asepart';" -U postgres

exit 0
