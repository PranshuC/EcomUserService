# EcomUserService

### Backend Projects: Authentication and Middlewares [11-12-23]
1. Implementation of basic User, Role & Session models
2. Implementation of basic User, Role & Auth controller methods


**Evolution of Authentication**
 - Basic login flow <br>
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


### Backend Projects: DB Scripts and Migration [18-12-23]
1. Flyway/ Liquibase for DB migration - simplifies managing and applying changes to database schema. <br>

**IntelliJ Flyway Settings :** <br>
IntelliJ Ultimate -> Persistence (side-icon) -> (+) -> <project>(right-click) -> New -> Flyway Init Migration <br>
(pop-up) Source Type: Model -> Save As: File; Destination: src/main/resources/db/migration; Name: V1_init_db.sql <br>
Destination path is important as Flyway will look for migration scripts. Verify the SQL scripts - as per Project need <br>

application.properties : spring.jpa.hibernate.ddl-auto=none <br>
With **none** value application comes up fine even if tables are deleted from DB.
So, at least **validate** should be the value (application starts only if verified). <br>
**create, create-drop, update** values aren't used in production code.

After adding Flyway script, run application to get tables created (drop previously created ones).
1 additional **flyway_schema_history** table to maintain the time & version of script run.
In production, CUD operations' access are removed on this table, only Read. <br>
**checksum** column in this table is hash of all changes to the script.
Ex : If script modified, anyone can cross-check checksum hash value mismatch & caught.
So, same-named script file will fail to run, mentioning tampered.


### Backend Projects: Jwt Decoding, Cookies, Csrf [02-01-24]
1. Authorization (Bearer), Cookies, CORS, CSRF

Postman app (any API) -> Authorization (Bearer Token) -> (automatically) -> New Header > Authorization: Bearer <token> <br>
(cURL has the Authorization header as well)

CORS (Cross-Origin Resource Sharing) - Browser security feature that allows web applications to access resources from a 
different domain than the one that served the page. Need to be disabled by default.

CSRF (Cross-Site Request Forgery) - An attack that forces authenticated users to submit a request to a Web application 
against which they are currently authenticated. Disabled.

