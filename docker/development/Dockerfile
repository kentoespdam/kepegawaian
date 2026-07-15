FROM ghcr.io/graalvm/jdk-community:25 AS base
WORKDIR /app

FROM base AS runner
COPY ./build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","-XX:+UseSerialGC","-Xmx512m","app.jar"]