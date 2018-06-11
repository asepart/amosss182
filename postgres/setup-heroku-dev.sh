#!/bin/sh

heroku pg:reset -a asepartback-dev --confirm asepartback-dev

heroku pg:psql -c "create extension pgcrypto;" -a asepartback-dev
heroku pg:psql -f schema.sql -a asepartback-dev
heroku pg:psql -f testdata.sql -a asepartback-dev
