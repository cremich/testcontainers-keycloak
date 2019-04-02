# Testing a spring security keycloak integration with testcontainers.org
[![Build Status](https://travis-ci.org/cremich/testcontainers-keycloak.svg?branch=master)](https://travis-ci.org/cremich/testcontainers-keycloak)
[![License](http://img.shields.io/:license-mit-blue.svg)](https://github.com/cremich/testcontainers-keycloak/blob/master/LICENSE)
[![versionspringboot](https://img.shields.io/badge/springboot-2.1.3_RELEASE-brightgreen.svg)](https://github.com/spring-projects/spring-boot)

This sample application demonstrates how to test a spring [keycloak](https://www.keycloak.org) integration with [testcontainers.org](https://www.testcontainers.org).

## What is Keycloak?
Keycloak is an open source Identity and Access Management powered by Redhat. The official documentation from Redhat describes:
> Add authentication to applications and secure services with minimum fuss. No need to deal with storing users or authenticating users. It's all available out of the box.
> You'll even get advanced features such as User Federation, Identity Brokering and Social Login.
> For more details go to about and documentation, and don't forget to try Keycloak. It's easy by design!

## What is Testcontainers?
From the official [testcontainers](https://testcontainers.org) homepage:

> Testcontainers is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

> Testcontainers make the following kinds of tests easier:

> * Data access layer integration tests: use a containerized instance of a MySQL, PostgreSQL or Oracle database to test your data access layer code for complete compatibility, but without requiring complex setup on developers' machines and safe in the knowledge that your tests will always start with a known DB state. Any other database type that can be containerized can also be used.
> * Application integration tests: for running your application in a short-lived test mode with dependencies, such as databases, message queues or web servers.
> * UI/Acceptance tests: use containerized web browsers, compatible with Selenium, for conducting automated UI tests. Each test can get a fresh instance of the browser, with no browser state, plugin variations or automated browser upgrades to worry about. And you get a video recording of each test session, or just each session where tests failed.
> * Much more! Check out the various contributed modules or create your own custom container classes using GenericContainer as a base.


## Application Setup
The application setup is simple:
It is a basic spring-boot application. Spring security is configured with a keycloak adapter to secure the 
application.

The actuator endpoints are secured and can only be used if the request contains a valid bearer token and the
user has the `monitoring` role


## Integration-Tests
The `KeycloakIntegrationTest` start a keycloak server, imports a predefined realm and creates some sample users. 
After the container ist successfully started, an access token can be generated and used to test the 
actuator endpoints.
