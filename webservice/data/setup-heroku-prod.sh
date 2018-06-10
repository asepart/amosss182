#!/bin/sh

heroku pg:reset -a asepartback --confirm asepartback
heroku pg:psql -f schema.sql -a asepartback
