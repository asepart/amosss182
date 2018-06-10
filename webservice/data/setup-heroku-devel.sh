#!/bin/sh

heroku pg:reset -a asepartback-dev --confirm asepartback-dev
heroku pg:psql -f schema.sql -a asepartback-dev
