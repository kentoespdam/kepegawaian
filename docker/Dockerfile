FROM amazoncorretto:21.0.2

WORKDIR /app

ARG JAR_FILE=*.jar

COPY ${JAR_FILE} ./app.jar

ENTRYPOINT ["java","-jar","-XX:+UseSerialGC","-Xmx512m","app.jar"]
