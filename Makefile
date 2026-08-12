.PHONY: build-dev
build-dev: bootJar
	docker buildx bake -f docker/development/docker-compose.yml

# Dockerfile meng-COPY build/libs/*.jar ke image — jar WAJIB fresh sebelum bake,
# kalau tidak layer COPY memakai cache jar lama (trappot stale-jar).
.PHONY: bootJar
bootJar:
	./gradlew bootJar

.PHONY: start-dev
start-dev:
	docker compose -f ./docker/development/docker-compose.yml up -d

.PHONY: stop-dev
stop-dev:
	docker compose -f ./docker/development/docker-compose.yml down

.PHONY: rebuild-dev
rebuild-dev:
	make stop-dev && make build-dev && make start-dev

.PHONY: build-prod
build-prod:
	docker compose -f ./docker/production/docker-compose.yml build

.PHONY: run-prod
start-prod:
	docker compose --env-file ./.env -f ./docker/production/docker-compose.yml up -d

.PHONY: stop-prod
stop-prod:
	docker compose -f ./docker/production/docker-compose.yml down

.PHONY: rebuild-jar
rebuild-jar:
	git pull && ./gradlew clean && ./gradlew bootJar