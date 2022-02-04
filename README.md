## Setup Vue.js & Spring Boot

### Prerequisites
#### Install Java
feel free to use these links or anything else. Project is set up with JDK11.
##### Windows
https://www.codejava.net/java-se/download-and-install-java-11-openjdk-and-oracle-jdk

##### Mac
https://medium.com/@mdchandhyd/install-java-11-in-mac-6adff940b73e

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
create a user with username `app_service` and password `4hRrc79KF4rUPv`
Make sure Postgres is listening on port 5432

### Running the App
To run the app, cd into app dir. From root directory of repo, `cd app`

Run the install script `mvn clean install`

Run the app `mvn --projects backend spring-boot:run`

To run frontend for development and get faster feedback, you can cd into frontend directory and run `yarn serve`

### Building the App
To build the app, cd into frontend dir. From root directory of repo, `cd app/frontend`

Run build script `yarn build`

Deploy frontend with maven install script:
- from app directory `mvn clean install`

Run the app with `mvn --projects backend spring-boot:run`

### User Creation
Navigate to http://localhost:8080/ in your browser. it should redirect you to http://localhost:8080/login. From there you can click on the link to sign up, enter the info and click sign up. if you get riderected to the home page, your user has been created and you have been logged in