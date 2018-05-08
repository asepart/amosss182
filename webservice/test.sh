#!/bin/bash

curl -i -u admin:admin -H "X-ASEPART-Role: Admin" -X GET http://localhost:12345/login
printf "\n"
