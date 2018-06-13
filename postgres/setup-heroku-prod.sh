#!/bin/sh

heroku pg:reset -a asepartback --confirm asepartback

heroku pg:psql -c "create extension pgcrypto;" -a asepartback
heroku pg:psql -f schema.sql -a asepartback
heroku pg:psql -f testdata.sql -a asepartback
