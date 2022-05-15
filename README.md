# CSC450-group-project

## Setup Vue.js & Spring Boot

### Prerequisites

#### Install Java

feel free to use these links or anything else. Project is set up with JDK 17.
https://www.codejava.net/java-se/download-and-install-openjdk-17

#### Install Maven

Project is set up with Maven 3.8.4
follow directions at https://maven.apache.org/install.html

##### Install Node

Doesn't matter how you get it but you need to have version 16.12.1. The maven script you will eventually run should install it for you but just in case.

##### Install Yarn

Node project is set up using yarn. If you are unfamiliar with yarn, anytime you see something saying to run `npm ...`, you will instead run `yarn ...`. Yarn seems to have a lot less problems with vulnerabilities in packages.

##### Install Vue CLI

for this, just run `npm install -g @vue/cli`. For this, because we are installing it globally, you will need to use npm.

after install, make sure it is up to date with `vue upgrade`

##### Install PostgreSQL

Project used version 14.
After installing postgres, create a db called `family_org_app`
Create a user with full priveliges
Add the username and password to the application.properties file backend/src/main/resources
Make sure Postgres is listening on port 5432

### Building the App

Run the build and install script `mvn clean install`. This will build the frontend, copy it to the backend to be served, compile the backend code into a runnable jar for deployment, and run all tests.

Some known issues that might pop up during build:

- Occassionaly yarn install command will fail, if that is the case, cd into the frontend directory and run `yarn install` manually before running `mvn clean install` again
- `yarn build` may fail if you have a node version > 16.x, make sure you are using a node version of 16 before running `mvn clean install` again

### Running the App

Run the app with `mvn --projects backend spring-boot:run` to run with the embedded tomcat server. Locally, anything that sends an email is disabled with the property `messaging.use.smtp`. To enable, this property will need to be set to true, and `messaging.email`, `messaging.username`, and `messaging.password` need to be populated with the appropriate values.

If you wish to not use the embedded tomcat server, a deployable jar file is produced during the build that can be ran from anywhere.

To test if the app is running, visit http://localhost:8080/ and you should be redirected to http://localhost:8080/login. If you don't already have a user created locally, you can create one and login to acces the app

To visit the production instance of the application, you can visit https://happyhome-organization.herokuapp.com.
