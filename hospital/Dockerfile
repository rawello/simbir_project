FROM gradle:8.10.2-jdk AS builder

WORKDIR /home/gradle/project

COPY . .

RUN gradle clean build --no-daemon

FROM openjdk:22-jdk-slim

WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/simbirsoft-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
