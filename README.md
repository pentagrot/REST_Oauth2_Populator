This application related to [REST_Oauth2](https://github.com/pentagrot/REST_Oauth2) project and cannot be used without it! <br><br>
To start this application you first need to compile it with javac.
```
javac Main.java
```
Then you can start a application by providing username and password for access token generation. <br>
User must be present in 'test_realm' realm within Keycloak.
```
java Main username password
```
