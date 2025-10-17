FROM amazoncorretto:21.0.2 AS base
WORKDIR /app
CMD ["tail", "-f", "/dev/null"]