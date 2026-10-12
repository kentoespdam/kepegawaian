package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("development")
@Transactional
class PrefRolePermissionMappingTest {

    @Autowired
    private PrefRoleRepository prefRoleRepository;

    @Test
    void shouldLoadPrefRolesWithoutEnumMismatch() {
        List<PrefRole> roles = prefRoleRepository.findAll();
        assertFalse(roles.isEmpty());
    }
}
