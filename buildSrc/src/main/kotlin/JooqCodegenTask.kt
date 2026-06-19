import org.flywaydb.core.Flyway
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target
import org.jooq.meta.jaxb.ForcedType

abstract class JooqCodegenTask : DefaultTask() {

    @get:Input
    abstract val jdbcUrl: Property<String>

    @get:Input
    abstract val dbUser: Property<String>

    @get:Input
    abstract val dbPassword: Property<String>

    @TaskAction
    fun generate() {
        val url = jdbcUrl.get()
        val user = dbUser.get()
        val password = dbPassword.get()

        val outDir = project.projectDir.resolve("src/main/java").absolutePath

        logger.lifecycle("Running Flyway migration against $url ...")
        Flyway.configure()
            .dataSource(url, user, password)
            .locations("filesystem:src/main/resources/db/migration")
            .load()
            .migrate()

        logger.lifecycle("Flyway migration complete. Generating JOOQ classes...")

        val config = Configuration()
            .withJdbc(
                Jdbc()
                    .withDriver("org.mariadb.jdbc.Driver")
                    .withUrl(url)
                    .withUser(user)
                    .withPassword(password)
            )
            .withGenerator(
                Generator()
                    .withDatabase(
                        Database()
                            .withName("org.jooq.meta.mariadb.MariaDBDatabase")
                            .withIncludes(".*")
                            .withExcludes("flyway_schema_history")
                            .withInputSchema("kepegawaian")
                            .withForcedTypes(
                                ForcedType()
                                    .withName("BOOLEAN")
                                    // Map MariaDB BIT(1) (default-generated as TINYINT/Byte
                                    // because MariaDB dialect aliases BIT(1) → TINYINT in
                                    // its JDBC type info) to Java Boolean for the soft-delete
                                    // column only. Source-of-truth remains DB schema.
                                    .withIncludeExpression("\\bis_deleted\\b")
                            )
                    )
                    .withTarget(
                        Target()
                            .withPackageName("id.perumdamts.kepegawaian.jooq")
                            .withDirectory(outDir)
                    )
            )

        GenerationTool.generate(config)

        logger.lifecycle("JOOQ code generation complete. Output: src/main/java/id/perumdamts/kepegawaian/jooq/")
    }
}
