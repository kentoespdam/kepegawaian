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

# ==============================================================================
# Data Migration Tooling (tools/migration)
# ==============================================================================
MIGRATE_PYTHON ?= $(shell if [ -f .venv/bin/python3 ]; then echo .venv/bin/python3; elif [ -f tools/migration/.venv/bin/python3 ]; then echo tools/migration/.venv/bin/python3; else echo python3; fi)

.PHONY: migrate-help migrate-venv migrate-preflight migrate-test migrate-run-all migrate-stage migrate-audit migrate-audit-integrity migrate-reconcile-payroll migrate-sync-files migrate-sync-files-dry migrate-sync-auth migrate-sync-auth-dry

migrate-help:
	@printf "\033[1;34m========================================================================\033[0m\n"
	@printf "\033[1;37m  Kepegawaian Data Migration Tooling (tools/migration)\033[0m\n"
	@printf "\033[1;34m========================================================================\033[0m\n"
	@printf "\033[1;33mUsage:\033[0m make [target] [STAGE=stage_name] [WORKERS=N]\n\n"
	@printf "\033[1;32mEnvironment & Setup:\033[0m\n"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-help" "Display this migration help menu"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-venv" "Initialize .venv and install tools/migration/requirements.txt"
	@printf "  \033[1;36m%-26s\033[0m %s\n\n" "migrate-test" "Run migration unit tests (tools/migration/tests)"
	@printf "\033[1;32mMigration Pipeline:\033[0m\n"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-preflight" "Run preflight check (Stage 0: DB & Appwrite connectivity)"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-run-all" "Execute full migration pipeline (Stage 0 to Stage 7)"
	@printf "  \033[1;36m%-26s\033[0m %s\n\n" "migrate-stage" "Execute specific stage (e.g. STAGE=stage2 or STAGE=stage4_cuti)"
	@printf "\033[1;32mAudit & Verification:\033[0m\n"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-audit" "Run full audit (referential integrity + payroll reconciliation)"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-audit-integrity" "Run referential integrity & Hibernate Envers audit only"
	@printf "  \033[1;36m%-26s\033[0m %s\n\n" "migrate-reconcile-payroll" "Run historical payroll reconciliation audit only"
	@printf "\033[1;32mFile Synchronization (Phase 2 Worker):\033[0m\n"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-sync-files-dry" "Simulate physical file attachment sync (dry-run)"
	@printf "  \033[1;36m%-26s\033[0m %s\n\n" "migrate-sync-files" "Run physical file sync worker (default WORKERS=4)"
	@printf "\033[1;32mAuthentication Provisioning:\033[0m\n"
	@printf "  \033[1;36m%-26s\033[0m %s\n" "migrate-sync-auth" "Provision employee accounts to Appwrite Auth"
	@printf "  \033[1;36m%-26s\033[0m %s\n\n" "migrate-sync-auth-dry" "Simulate employee account provisioning to Appwrite Auth (dry-run)"
	@printf "\033[1;34m========================================================================\033[0m\n"

migrate-venv:
	@if [ ! -d .venv ]; then \
		echo "Creating virtual environment in .venv..."; \
		python3 -m venv .venv; \
	fi
	@echo "Installing dependencies from tools/migration/requirements.txt..."
	@.venv/bin/pip install -r tools/migration/requirements.txt
	@echo "Virtual environment ready at .venv"

migrate-test:
	$(MIGRATE_PYTHON) -m unittest discover -s tools/migration/tests -v

migrate-preflight:
	$(MIGRATE_PYTHON) tools/migration/run.py stage --name stage0_preflight

migrate-run-all:
	$(MIGRATE_PYTHON) tools/migration/run.py run-all

migrate-stage:
	$(MIGRATE_PYTHON) tools/migration/run.py stage --name $(or $(STAGE),stage0_preflight)

migrate-audit:
	$(MIGRATE_PYTHON) tools/migration/run.py audit

migrate-audit-integrity:
	$(MIGRATE_PYTHON) tools/migration/run.py audit --integrity-only

migrate-reconcile-payroll:
	$(MIGRATE_PYTHON) tools/migration/run.py audit --payroll-only

migrate-sync-files-dry:
	$(MIGRATE_PYTHON) tools/migration/run.py sync-files --dry-run

migrate-sync-files:
	$(MIGRATE_PYTHON) tools/migration/run.py sync-files --workers $(or $(WORKERS),4)

migrate-sync-auth:
	$(MIGRATE_PYTHON) tools/migration/run.py sync-auth

migrate-sync-auth-dry:
	$(MIGRATE_PYTHON) tools/migration/run.py sync-auth --dry-run
