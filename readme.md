# Spring Cloud Gateway sample
Services to study the features of the Spring-cloud-gateway module.


It contains 3 applications:
 - customer-service: A service that expose 3 endpoints for retrieving data from a database
 - eureka-service: A service that is used as the Eureka Server for registering the other services
 - edge-service: The gateway service. Contains the spring-cloud-gateway features like basic and load-balanced aware proxies and filters.


## Build & Run
Run all the services locally and start the database instance running the following command:
`docker run -d -p 27017:27017 -v ~/data:/data/db mongo`
