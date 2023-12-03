# Project structure: 

 - **ms-tula-db**: Database schema module 
 - **ms-tula-api**: API module
 - **ms-tula-impl**: Microservice implementation 

# ms-tula-db

Must contain all database-related schemas\changelogs\scripts etc. 
Must support execution separately from source code build for CI\CD purposes

By default, liquibase configuration is presented and can be executed with ```mvn liquibase:update``` command

# ms-tula-api

Must contain all API contracts microservice exposes including OpenAPI, AsyncAPI, WSDL\XSD, Avro schemes, DTOs etc
Must not contain anything except specifications and DTOs

By default, openapi code generation with spring profile is configured(DTOs and REST API interfaces)

# ms-tula-impl

Must contain all business logic of service
Must not contain any DDL logic or expose API contracts
