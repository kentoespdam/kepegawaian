.PHONY: build-dev
build-dev:
	docker buildx bake -f ./docker/development/docker-compose.yml

.PHONY: run-dev
start-dev:
	docker compose -f ./docker/development/docker-compose.yml up -d

.PHONY: stop-dev
stop-dev:
	docker compose -f ./docker/development/docker-compose.yml down

.PHONY: build-prod
build-prod:
	docker compose -f ./docker/production/docker-compose.yml build

.PHONY: run-prod
start-prod:
	docker compose -f ./docker/production/docker-compose.yml up -d

.PHONY: stop-prod
stop-prod:
	docker compose -f ./docker/production/docker-compose.yml down
