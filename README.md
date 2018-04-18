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

`installing iojs`

`$ cd admin-web-app`

`$ npm start`

You can now view admin-web-app in the browser on http://localhost:3000/

#### Dependencies

To run the Webapp you need to install `iojs`
`$ npm install iojs`
