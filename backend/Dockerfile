FROM gradle:jdk23-corretto-al2023 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
COPY generated/ ./generated

RUN gradle build -x test -x checkstyleMain -x checkstyleTest

FROM eclipse-temurin:23-jre-alpine

COPY --from=builder /app/build/libs/hotel-booking-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]