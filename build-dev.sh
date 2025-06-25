#!/bin/bash
./gradlew bootJar && docker buildx bake && docker compose up -d