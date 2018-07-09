# amosss182

[![Build Status](https://travis-ci.org/asepart/amosss182.svg?branch=master)](https://travis-ci.org/asepart/amosss182)
[![codecov](https://codecov.io/gh/asepart/amosss182/branch/master/graph/badge.svg)](https://codecov.io/gh/asepart/amosss182)

The team develops an app to manage testdrivers.
The app has the following main features:
* Android App
* Java-based server backend
* Secure chat between clients
* Problem reports via app

## Database

We use PostgreSQL as our database. To run the web service on your machine, a PostgreSQL instance must be installed and configured first. For running locally on your local machine, using a docker image is recommended. To do that, run the script `setup-docker.sh` inside the `database` directory with root privileges. This will install the PostgreSQL docker image, start a container on default port 5432, and handle database configuration. `cleanup-docker.sh` will stop the database container and delete it.

## Web Service Backend

For running the web service locally, it is recommended to build and run the web service docker container. Execute the script `run-docker.sh` inside the `webservice` directory with root privileges (call `stop-docker.sh` to revert). You can also run the web service as a user process with `mvn exec:java`. JUnit tests can be executed by running `mvn test` inside the `webservice` directory.

The environment variable `ASEPART_POSTGRES_HOST` can be set to change the default hostname of the database the web service tries to connect to. Default is localhost. If `JDBC_DATABASE_URL` is set, the web service will use that and ignore all other configuration options.

For the file upload feature, we use a Minio server, which is an Open Source S3-compatible file storage server. The environment variables `ASEPART_MINIO_URL`, `ASEPART_MINIO_BUCKET`, `MINIO_ACCESS_KEY` and `MINIO_SECRET_KEY` must be set to enable this feature.

## Admin Web App

The admin web application can be build and executed by:

`$ cd admin-web-app`

`$ npm install`

`$ npm start`

Your browser will be opened displaying the web app.

## User App

### Android

To build the app you have to change the directory to `user-app` and run

`$ npm install`

The command `$ npm start` fails at the moment, therefore it is not possible to run the app using Expo.

In order to test the app, one should have a running Android Emulator or a connected Android phone in the debugging mode.

`$ npm run android`
