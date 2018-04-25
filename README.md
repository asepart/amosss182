# amosss182

The team develops an app to manage testdrivers.
The app has the following main features:
* Android App
* Java-based server backend
* Secure chat betweeen clients
* Problem reports via app

## Building

### Web Service Backend

Currently, there is a demo REST service which can be build and executed using:

`$ cd webservice`

`$ mvn clean compile`

`$ mvn exec:java -Dexec.mainClass="HelloWorldService"`

On port 12345, the service will respond with "Hello World" to any GET request.

### Admin Web App

The demo Web Application can be build and executed by:

`$ cd admin-web-app`

`$ npm install`

`$ npm start`

You can now view admin-web-app in the browser on http://localhost:3000/

### User App

#### Android

To build the app one have to change the directory to `user-app` and run

`npm install`

`npm run eject`

`npm run android`

## Testing

### Web Service Backend

JUnit is used as our testing framework. To run the tests, execute:

`$ cd webservice`

`$ mvn test`

### Admin Web App

The web application uses Jest to run unit and rendering tests. To run from command line, execute:

`$ cd admin-web-app`

`$ npm install`

`$ npm test`

### User App

Like the web application, the mobile user app also uses Jest.

`$ cd user-app`

`$ npm install`

`$ npm test`
