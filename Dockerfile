FROM gradle:8.6-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean buildFatJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
