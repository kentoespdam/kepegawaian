package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiIndexQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Regression: GET /master/organisasi/ (no params) must not NPE on `switch(query.getSortBy())`.
 * Root cause: switch-on-String throws NullPointerException when getSortBy() returns null.
 */
@SpringBootTest
@ActiveProfiles("development")
class OrganisasiNpeRepro {
    @Autowired private OrganisasiQueryRepository repo;

    @Test
    void listWithDefaultSortShouldNotNpe() {
        var q = new OrganisasiIndexQuery();  // sortBy == null (default)
        Page<?> result = assertDoesNotThrow(() -> repo.pageQuery(q),
                "pageQuery() must not throw when sortBy is null");
        assertNotNull(result);
    }
}
