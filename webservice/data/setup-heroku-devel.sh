#!/bin/sh

heroku pg:reset
heroku pg:psql -f schema.sql -a asepartback-dev
