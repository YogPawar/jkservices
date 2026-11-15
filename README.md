# ShetiLedger — MVP starter

Family-based agricultural work, billing and payment ledger.

## Stack
- Java 21, Spring Boot 3.5.7, Maven
- MySQL 8.4, Flyway, Spring Data JPA
- React + JavaScript + Vite + Material UI

## Run locally
0. `docker compose --env-file .\config.env up -d mysql`
1. `docker compose up -d mysql`
2. Backend: `cd backend && mvn spring-boot:run`
3. Frontend: `cd frontend && npm install && npm run dev`

## Business model
- The first registration creates a family workspace and OWNER user.
- Additional users join with a family invite code and remain PENDING until approved.
- Every business record is family-scoped.
- Work entries and invoices are separate: unbilled work appears in Pending Billing.
- An invoice contains work entries. Payments can be allocated to invoices or left as farmer advances.
- Overdue = due date passed AND invoice balance > 0.

## Important security note
This is an initial development scaffold, not a production-ready deployment. JWT login, role enforcement, tenant-scoped repositories, invite-code rotation, approval endpoints, and audit interceptors must be completed before exposing it publicly. Never trust a family ID supplied by the browser; resolve tenant membership from the authenticated principal.


## Imp Commands
docker exec -it shetiledger-mysql mysql -u sheti -p
shetipassword


# Production running 

$env:SPRING_PROFILES_ACTIVE = "prod"
$env:DB_URL = "jdbc:mysql://localhost:3307/shetiledger?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
$env:DB_USERNAME = "sheti"
$env:DB_PASSWORD = "shetipassword"



### Packaging 

jpackage `
  --type app-image `
--name ShetiLedger `
  --app-version 1.0.0 `
--vendor "ShetiLedger" `
  --input .\deployment\package-input `
--main-jar shetiledger.jar `
  --dest .\deployment\app-image `
--java-options "-Dspring.profiles.active=prod" `
--win-console


### Maven  Command 
    mvn spring-boot:run "-Dspring-boot.run.profiles=dev"

 ## Production Run 
    cd D:\jkservice\backend

    $env:DB_URL = "jdbc:mysql://localhost:3307/jkservicedb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
    $env:DB_USERNAME = "jk"
    cd D:\jkservice\backend
    $env:DB_PASSWORD = Read-Host "Enter database password"
    
    mvn spring-boot:run "-Dspring-boot.run.profiles=prod"

## Docker Commands
    docker compose --env-file .\config.env config

## Swagger 
    http://localhost:8080/v3/api-docs
    http://localhost:8080/swagger-ui/index.html