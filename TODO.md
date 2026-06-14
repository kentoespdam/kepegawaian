# TODO - Flyway upgrade & config fix

- [ ] Update `build.gradle.kts` to align Flyway Gradle plugin + Flyway artifacts to the latest version (single source of truth via `flywayVersion`).
- [ ] Fix Flyway configuration in `build.gradle.kts` (migration `locations` and driver/URL handling) for newer Flyway expectations.
- [ ] Verify by running `./gradlew flywayInfo` (and optionally `./gradlew flywayMigrate` if appropriate).