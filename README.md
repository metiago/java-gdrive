## Java Google Drive

This is a web API which expose some methods to manage files in Google Drive.

The consent is given via Google OAuth2 during the authentication phase for any user who accept the
requested permissions.

#### Compile and running

Below there are the command to build and run this application
```bash
# compile
mvn clean package

# run
java -jar target/zbx-1.0-SNAPSHOT.jar

# deploy to GAE
mvn clean package appengine:deploy
```