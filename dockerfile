FROM openjdk:11
WORKDIR /ToyMB
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY HealthCheck.java HealthCheck.java
RUN ./mvnw clean install -DskipTests

ENTRYPOINT ["java","-jar","target/ToyMB-0.0.1-SNAPSHOT.jar"]