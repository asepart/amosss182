#!/bin/sh

psql -c "create extension pgcrypto;" -U postgres
psql -f schema.sql -U postgres

psql -c "alter user postgres with encrypted password 'asepart';" -U postgres

exit 0
