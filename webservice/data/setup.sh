#!/bin/sh

psql -c "create database asepartdb;" -U postgres

psql -d asepartdb -c "create extension pgcrypto;" -U postgres
psql -d asepartdb -f schema.sql -U postgres

psql -c "alter user postgres with encrypted password 'asepart';" -U postgres

exit 0
