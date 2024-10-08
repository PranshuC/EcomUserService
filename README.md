# ECommerce Project - User Service

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


### Backend Projects: O Auth2 Authorization Server [15-01-24]
1. Build OAuth2 Authorization Server <br>
Dependency : spring-boot-starter-oauth2-authorization-server <br>
Class CustomSpringUserDetails (implement UserDetails) - needed for OAuth <br>
CustomSpringUserDetailsService (implement UserDetailsService) - loadUserByUsername

SpringSecurity -> class for basic auth -> login(email + password) based <br>
SecurityConfig -> OAuth based configuration

OpenID Connect : Industry standard for all the steps inside Authentication & Authorization for OAuth based protocols. <br>
Refresh token : allows to have short-lived access tokens without having to collect credentials every time one expires.

Become a registered client at Google's Auth Service (configuration) <br>
Here, ProductService is client to UserService


### Backend Projects: Finishing Our Authentication Service [16-01-24]
1. Security Models : Authorization, AuthorizationConsent <br>
AuthorizationConsent : User's consent to share specific authorities with a client. Ex : Google pop-up
2. Security Models : Client, Authorization, AuthorizationConsent
3. Security Repositories : ClientRepository, AuthorizationRepository, AuthorizationConsentRepository, JpaRegisteredClientRepository
4. Security Services : JpaOAuth2AuthorizationService, JpaOAuth2AuthorizationConsentService
5. V3__addSpringSecurityTables.sql : Flyway script to create Client, Authorization, AuthorizationConsent tables

RegisteredClientRepository : Spring Security's default implementation uses InMemory Arrays.asList Repository <br>
But we need to use DB to save the multiple client details. <br>
So, we'll use security.repository.JpaRegisteredClientRepository for all client related activities.

http://localhost:9090/login - comes from Spring Security package

References : <br>
https://docs.spring.io/spring-authorization-server/reference/getting-started.html <br>
https://www.toptal.com/spring/spring-boot-oauth2-jwt-rest-protection <br>
https://github.com/spring-projects/spring-security-samples/tree/6.2.x/servlet/spring-boot/java/oauth2/login <br>
https://medium.com/@tanmaysaxena2904/spring-security-the-security-filter-chain-e09e1f53b73d

Fundamental APIs for auth are already given by Spring Security OAuth2
1. /login
2. /logout
3. /signup
4. /validate -> to validate the token from Resource Service to Auth Service
5. /register -> registering a new client [ basically the service which will rely on Auth Service for authentication ]

Companies have Central AuthService (OAuth) <br>
User login -> customer login <br>
All the micro-services will be client to this central service

Employees login <br>
Framework : SSO (Single Sign On) -> single way to sign-in everyone <br>
Implementation : LDAP(Lightweight Directory Access Protocol) based authentication <br>
Only knowing username-password wouldn't help, Hardware-based authentication <br>
New company laptop has a directory with user details, roles, keys <br>
(Active Directories -> only the admin user of the device can access) <br>
While login, hardware information is verified by the service <br>
Sometimes, VPN is used and hardware verification is done via apps like Duo Mobile.

Homework -> Explore how GitHub login from terminal works, Explore how terminal talks to other services like GitHub


### Backend Projects: Dockerizing Applications [22-01-24]
1. Introduction to AWS console. Explain EC2 & RDS services.
2. Plan for next classes about creating RDS instances and connecting to apps.


### Backend Projects: Deploying Applications To AWS [23-01-24]
1. Create user-service-db RDS (PostgreSQL DB) in AWS. <br>
Free Tier(db.t3.micro), ap-southeast-2a & Public accessible <br>
Security Group > Inbound rule > All traffic from anywhere
2. Connect with EcomUserService using spring.datasource.url, username & password <br>
   (AWS (RDS) > Connectivity & security > Endpoint & Port)
3. Create Elastic Beanstalk environment for EcomUserService


### Backend Projects: Implementing Payment Gateway 1 [25-01-24]
1. EC2 instance(t2.micro) for UserService <br>
   (Ubuntu; Allow SSH, HTTP, HTTPS; ap-southeast-2b) <br>
   (Edit Inbound rules (Add rule) > Type : All TCP, Source : Anywhere IPv4 OR Custom TCP -> Port 8080)
2. Static IPs : Networking and Security > Elastic IPs (Associate address to EC2 instance)
3. Docker - DockerFile
   https://aw.club/global/en/blog/how-to-dockerize-spring-boot-application


### Backend Projects: Implementing Search: Paging, Sorting, Elastic Search [29-01-24]
1. Use customAPIs to connect ProductService with UserService
2. Verify the UserService > AuthController "/validate "API.
3. Validate User by decoding Token > Payload

OAuth2Service does make sense, if Google-type log-in option is required. <br>
But, we just need internal-connect between microservices, so custom APIs give flexibility <br>
to implement LDAP server etc.

Token should never be validated in Service layer. Do in Controller, better even before it using SpringSecurity


### Backend Projects: Creating Payment Microservice, Cron And Webhooks [30-01-24]
1. Validate API completed : ProductService > UserService
2. Create **RDS** in AWS for ProductService & UserService. <br>
   (Creating > Configuring-enhanced-monitoring > Backing-up > Available)
3. Create 2 **EC2** instances. Use .pem file to connect to EC2 instance. <br>
   (SSH into EC2 instance, Install Java, JAVA_HOME path, tomcate, maven)
```shell
sudo apt-get update
sudo apt install default-jdk
java -version #11.0.21 - default from apt
sudo nano /etc/environment # JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64/bin/java"
#sudo apt-get install tomcat9
wget <downloads.apache/tomcat-9 link>
tar -zvxf apache-tomcat-9.0.85.tar.gz
sudo apt install maven
```
4. (If problem above) Create **Elastic Beanstalk** (managed-EC2, configured auto-scaling) <br>
   (Web server environment, Platform(Managed):Tomcat, Application code:Local file(project's war), Single instance(free tier eligible)) <br>
   (Configure service access > EC2 key pair(.pem file), Architecture:arm64(cheaper))

AOP Concept - @ControllerAdvice [GlobalControllerAdvice.class] <br>
This will be called when globally anywhere Exception occurs (InvalidTokenException,TokenExpiredException)

AWS Lambda -> FunctionAsAService (FaaS), Serverless (Server not occupied all-time), cold-start mode (won't be up, unless called), down most time(cheaper)


### Backend Projects: Notification Service [28-02-24]
1. Amazon **Simple Email Service** (SES) : Create a domain, verify to send bulk-notifications

References : <br>
https://www.baeldung.com/spring-kafka <br>
https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka <br>
https://reflectoring.io/spring-cloud-aws-ses/ <br>
https://github.com/MLP-07/Notification-Service (Sample Notification Service) <br>
https://stackoverflow.com/questions/42471870/publish-subscribe-vs-producer-consumer


### Backend Projects: Implementing Kafka [29-02-24]
1. Install Kafka locally : brew install kafka <br>
(Start Zookeeper, Start Kafka, Create Topic, List Topics, Start Producer, Start Consumer)
2. config/KafkaProducerConfig <br>
This is a Producer for Kafka. Whenever we need to add an event to Kafka, we'll use this Producer.
3. SendEmailDto (Email Notification Details), AuthService.signUp (Kafka message sent on successful sign-up) <br>
sendEmailDto.setFrom -> needs to be domain owner's email id

Why message being sent to Kafka is String? <br>
Message is sent in a serialized form. We'll have to serialize the object before sending it to Kafka using ObjectMapper.

References : <br>
https://learn.conduktor.io/kafka/how-to-install-apache-kafka-on-mac/ <br>
https://www.baeldung.com/jackson-object-mapper-tutorial <br>
https://support.google.com/a/answer/176600 (Send email via Gmail Server) <br>
https://www.digitalocean.com/community/tutorials/javamail-example-send-mail-in-java-smtp


### Backend Project: Spring Cloud [05-03-24]
1. EcomServiceDiscovery project : Eureka server
2. eureka-client dependency, application.properties : eureka.client...=true <br>
   (spring.application.name property is required for Eureka to recognize MS)
2. Edit Configurations > Environment Variables : ${SERVER_PORT} -> dynamic diff values <br>
   (Running multiple instances with different ports : Multiple registrations in Eureka server)


