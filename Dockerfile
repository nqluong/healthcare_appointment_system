FROM maven:3.9.9-ibm-semeru-17-noble AS build

WORKDIR /app

COPY . .

RUN mvn install -DskipTests=true

FROM alpine:3.22.1

RUN apk add openjdk17

WORKDIR /run

COPY --from=build /app/target/healthcare_appointment-0.0.1-SNAPSHOT.jar /run/healthcare_appointment-0.0.1.SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "healthcare_appointment-0.0.1.SNAPSHOT.jar"]