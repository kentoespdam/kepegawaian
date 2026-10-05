package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class ProfileUpdateQueryServiceReproTest {

    @Autowired
    private RevInfoService revInfoService;

    @Test
    void findLatestRevision_withoutTransaction_shouldReproduceEntityManagerClosed() {
        assertDoesNotThrow(() -> revInfoService.findLatestRevision(Biodata.class, "1234567890123456"));
    }
}
