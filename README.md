# EcomUserService

### Backend Projects: Authentication and Middlewares [11-12-23]
1. Implementation of basic User, Role & Session models
2. Implementation of basic User, Role & Auth controller methods
   <br><br>
   Evolution of Authentication
- Basic login flow
- Store password in encoded manner <br>
   a. Imported Spring Security <br>
   b. SpringSecurity Configuration class <br>
   c. bCrypt matcher to compare passwords <401 Unauthorised in Postman>

### Backend Projects: Implementing Auth Service [14-12-23]
1. Implementation of SpringSecurity & SecurityFilterChain
2. Implementation of Session token (random string)
3. Test DB connection and Auth services with Postman

MultiValueMapAdapter is the recommended Data Structure for Headers. It is a map with single key and multiple values. <br>
| Key    | Value |
|--------|-------|
| Token  | ""    |
| Accept | application/json, text, images |

"Set-Cookie" is an industry standard key-name. Whenever client calls the server, it expects a token in this response header.

SpringSecurity doesn't allow any non-authorized API to be accessed without a token.
So, need SecurityFilterChain to allow few APIs to be accessed without a token.

### Backend Projects: Creating Payment Microservice, Cron and Webhooks [16-12-23]
1. Use MacAlgorithm(HS256) & SecretKey to generate a JWT token.

Auth Service doesn't do API call to fetch roles of user. It gets roles from the token itself (claims). <br>
Don't need to validate the entire body of the token. <br>
Frontend's job to store the token and check for its expiry. <br>
When SSO using Azure LDAP, 1 SecretKey against 1 user.
Multiple sessions of the user will be validated against 1 SecretKey. <br>
https://jwt.io/ - Website to see decoded JWT token.

