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

`$ npm start`

The console outputs an QR code which can be scanned using the "Expo" app which must be installed from the Play Store.

#### Workaround to get the react-native-camera working on Android

`$ npm run android`     // It will most probably fail, but we need to have the /Android folder

`$ npm install react-native-cli`

`$ react-native link react-native-camera`

`$ react-native upgrade`    // Answer no to all the answers

Modify the following files:

**android/build.gradle**
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url "$rootDir/../node_modules/react-native/android"
        }
        google()
    }
}


subprojects {
  project.configurations.all {
     resolutionStrategy.eachDependency { details ->
        if (details.requested.group == 'com.android.support'
              && !details.requested.name.contains('multidex') ) {
           details.useVersion "27.1.0"
        }
     }
  }
}
````

**android/gradle/wrapper/gradle-wrapper.properties**

`distributionUrl=https\://services.gradle.org/distributions/gradle-4.1-all.zip`

**android/app/build.gradle**
```
android{
  compileSdkVersion 27
  buildToolsVersion "27.0.0"
  
  .....
}
```

**Then run:**

` $ react-native link react-native-camera`

` $ npm run android `
