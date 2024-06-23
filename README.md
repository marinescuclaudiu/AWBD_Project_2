# Aplicatii Web pentru baze de date

## Project, 2nd Semester

> Name of the project: E-Commerce
> 
> Student 1: Duță Flavia
> 
> Student 2: Marinescu Claudiu
> 
> Grupa: 406
> 
> FMI, UniBuc

This project uses 3 microservices:
1. PRODUCT-SERVICE
2. INVENTORY-SERVICE
3. ORDER-SERVICE

Used ports:
- products: 8080
- order: 8081
- inventory: 8083, 8084
- config: 8070
- eureka: 8761
- gateway: 8071

-----

### Requirements

1. Microservices configuration management using Spring Cloud Config Server ✅
2. Using Feign Rest Client ✅
3. Service discovery --- Naming Server Eureka ✅
4. Intelligent routing, load balancing –-- Cloud Load Balancer ✅
5. Visibility: monitoring services/servers Zipkin Distributed Tracing Server ✅
6. Fault tolerance: Resilience4j default behavior in case of failure. ✅
7. Deploy with Docker and Kubernetes. ❌
8. Add swagger documentation of the API. ✅
9. Add error handling. ✅
10. Add HATEOS links. ✅
11. ***WebFlux (optional) ❌
