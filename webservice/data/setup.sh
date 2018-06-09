#!/bin/sh

if [ $USER != "postgres" ]; then
    echo "Script must be executed as user postgres!"
    exit 1
fi

createuser asepart
createdb asepartdb

psql -c "alter user asepart with encrypted password 'asepart';"
psql -c "grant all privileges on database asepartdb to asepart;"

psql -d asepartdb -c "create extension pgcrypto;"

exit 0
