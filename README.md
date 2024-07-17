# RateLimiterJava
Project to restrict request for User

## Purpose
The purpose of this project is administrate all request for a user to limit a any value

## What's inside
The microservices have the business logic for:
- CRUD operations for a User
- Endpoint to execute a request 
- Endpoint to get a resume for a user requests

## Plugins
  * Spring Boot
  * Lombok
  * Mapstruct
 
## Setup
Go to application.properties file and configure this properties:
+ *regularLimit*
+ *premiumLimit*
+ *timeWindowInSeconds*
+ *maxConsecutive429*
+ *blockTimeInSeconds*

## Building and deploying the application
First build the Docker image:
`docker build --build-arg JAR_FILE=build/libs/*.jar -t booster-java-sr/rate-limiter .`

Then run on Docker:
`docker run -p 8080:8080 booster-java-sr/rate-limiter`

### Building the application
Execute: `gradlew build`

### Running the application
Execute: `gradlew bootRun`

## License
This project is licensed under the GNU General Public License - see the [LICENSE](LICENSE) file for details