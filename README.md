ECommerce Microservices Platform

A production-style, cloud-native eCommerce backend built using Spring Boot Microservices and Spring Cloud.
This project demonstrates service discovery, centralized configuration, API gateway security, distributed tracing, event-driven communication, inter-service calls, and containerized deployment using Docker.

Key Engineering Concepts Demonstrated :

--> Microservices Architecture
--> API Gateway Pattern
--> Secure Gateway with Keycloak
--> Centralized Configuration Management
--> Service Discovery with Eureka
--> Inter-service communication using OpenFeign
--> Event-driven architecture using Kafka
--> Distributed tracing with Zipkin
--> Database per service pattern
--> Dockerized local development

Client
   |
   v
Keycloak (Auth Server - 8181)
   |
   v
API Gateway (Spring Cloud Gateway)
   |
   v
------------------------------------------------
| User Service | Product Service | Order Service |
------------------------------------------------
   |              |                |
   |              |                |
   v              v                v
 MySQL          MySQL             MySQL

Service Discovery → Eureka Server
Configuration     → Config Server (GitHub)
Tracing           → Zipkin (9411)
Messaging         → Kafka + Zookeeper

--> Core Components

--> API Gateway (Spring Cloud Gateway)
      Single entry point for all client requests
      Integrated with Keycloak for authentication and authorization
      Routes requests to backend services using Eureka service discovery
      Handles cross-cutting concerns like security and routing
    Port: 8080

--> Keycloak (Authentication & Authorization)
      Centralized identity and access management
      Integrated at the API Gateway layer
      Secures all downstream microservices
      Runs as a Docker container
    Port: 8181

--> Eureka Server (Service Discovery)
      All microservices register dynamically
      Enables load-balanced service-to-service communication
      Removes hardcoded service URLs
    Port: 8761

--> Config Server (Centralized Configuration)
      Externalized configuration for all services
      All application.yml files are maintained in a GitHub repository
      Services fetch configuration at startup

--> Business Microservices

--> User Service
      Manages user-related operations
      Uses MySQL as the database
      Registers with Eureka
      Configuration sourced from Config Server

--> Product Service
      Manages product catalog and inventory
      Uses MySQL
      Exposed via API Gateway
      Participates in inter-service communication

--> Order Service
      Handles order creation and processing
      Uses MySQL
      Communicates with:
      User Service
      Product Service
      Inter-service calls implemented using OpenFeign
      Publishes events to Kafka after order placement

--> Notification Service
    Event-driven microservice
    Listens to Kafka topics
    Processes order-related events
    Demonstrates asynchronous communication

--> Distributed Tracing
  Zipkin
      Enabled across all microservices
      Tracks request flow across services
      Helps in debugging and performance analysis
      Runs as a Docker container
    Port: 9411

--> Messaging & Event Streaming
    Kafka + Zookeeper
      Used for asynchronous, event-driven communication
      Order Service publishes events
      Notification Service consumes events
      Both Kafka and Zookeeper run via Docker

--> Databases
    MySQL used by:
        User Service
        Product Service
        Order Service
    Each service owns its database (database per service pattern)


| Component       | Port |
| --------------- | ---- |
| API Gateway     | 8080 |
| User Service    | 8081 |
| Product Service | 8082 |
| Order Service   | 8083 |
| Eureka Server   | 8761 |
| Config Server   | 8888 |
| Keycloak        | 8181 |
| Zipkin          | 9411 |


Run Locally
git clone https://github.com/Nchandramani/eCommerce-microservices.git
cd eCommerce-microservices
docker compose up -d

--> Why This Project Matters
This project closely mirrors real-world enterprise backend systems and demonstrates:
Scalability
Observability
Security
Maintainability
Cloud-native design principles
It is suitable as:
A portfolio project
A system design reference

--> Author
Chandramani Nishad
GitHub: https://github.com/Nchandramani

License
This project is intended for learning, demonstration, and portfolio purposes.
