#!/bin/sh

psql -c "create database asepartdb;" -U postgres
psql -c "create user asepart with encrypted password 'asepart';" -U postgres
psql -c "grant all privileges on database asepartdb to asepart;" -U postgres
psql -d asepartdb -c "create extension pgcrypto;" -U postgres
psql -d asepartdb -f schema.sql -U postgres

exit 0
