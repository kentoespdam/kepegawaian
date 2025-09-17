FROM amazoncorretto:21.0.2 AS base
WORKDIR /app

FROM base AS builder
COPY . .
RUN ./gradlew clean build -x test

FROM base AS runner
COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","-XX:+UseSerialGC","-Xmx512m","app.jar"]
