FROM ghcr.io/graalvm/jdk-community:25

WORKDIR /app
ARG JAR_FILE=*.jar
COPY build/libs/${JAR_FILE} ./app.jar

ENTRYPOINT ["java","-jar","-XX:+UseSerialGC","-Xmx512m","app.jar"]