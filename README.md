# Configuring Spring Boot REST API as an OAuth Resource Server

In order to set up a Spring Boot REST API as an OAuth Resource Server, we need to perform a few key steps:

## Configuration in application.yaml
The first step in setting up an OAuth Resource Server is to provide the OAuth configuration in the **application.yaml** file. Here's what you need to provide:
 
We first need to add the oauth configuration in the "application.yaml file". we need to give the following 
- **issuer-uri**: This is the URI to the identity provider or authority, such as Keycloak. The Resource Server uses this URI to verify that all access tokens have been issued by this authority.
- **jwk-set-uri**: This is the URI to the JSON Web Key set of the authority, which contains its public keys. These keys are used by the Resource Server to verify the signature of the access tokens.
- **audiences**: These are the values of the "aud" claim that Spring should expect in the access tokens. The "aud" claim represents the intended audience of the token, in other words, the application for which the token was issued.
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-identity-provider
          jwk-set-uri: https://your-identity-provider/.well-known/jwks.json
          audience: your-application
```


## Configuring the Security Filter Chain 
After setting up the application.yaml, the next step is to configure a SecurityFilterChain. This allows you to specify which routes are authenticated and which roles are allowed to access those routes.

In a SecurityFilterChain, you can match requests on any request attribute, such as a path pattern, HTTP method, or a header, and then specify the security constraints for those requests.

Here's an example configuration in a SecurityConfig class:
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    return http.build();
}
```

## Resource Server Role
By configuring our Spring Boot REST API as an OAuth Resource Server, we shift its responsibilities in the OAuth ecosystem. Unlike an OAuth Client, our application now solely consumes and validates access tokens to manage access to its resources.

As a Resource Server, our application does not deal with user credentials or token requests, which are handled by the OAuth Client and Authorization Server. Instead, its primary function is to authenticate and authorize requests based on access tokens. This narrows our security concerns and optimizes our application to effectively leverage OAuth's token-based authorization.

# Integration and Testing

After configuring the OAuth Resource Server and defining our routes, we can now integrate and test our application using a pre-configured Keycloak server and Postman.

## Pre-configured Keycloak Server

A Keycloak server is available and can be initiated using `docker-compose up`. It has been pre-configured with three users, each assigned a different role:

- `test1` -> role: `user`
- `test2` -> role: `admin`
- `test3` -> role: `moderator`

All users share the same password: `test`.

## Testing Using Postman

A Postman collection has been prepared for testing purposes. You can find this collection, along with the `docker-compose` file for Keycloak, in the root directory of the project.

## Running the Application

To run the Resource Server application, execute the following commands from the root directory of the project:

1. Compile the application: `mvn clean package`
2. Run the application: `java -jar target/resource-server-demo-0.0.1-Snapshot.jar`

## API Endpoints

Three routes have been defined in the `HomeController` class for testing:

- `@GetMapping("/")`: Returns a greeting message to a regular user.
- `@GetMapping("admin")`: Returns a greeting message to an admin.
- `@GetMapping("moderate")`: Returns a greeting message to a moderator.

These endpoints can be used to verify the access controls enforced by our OAuth Resource Server.

To send requests to these endpoints using Postman, ensure to include a valid access token obtained from Keycloak in the `Authorization` header. To obtain a new access token in Postman, simply click on "Authorization", then "Get New Access Token", and finally "use Access Token". The provided Postman collection will guide you through this process.
