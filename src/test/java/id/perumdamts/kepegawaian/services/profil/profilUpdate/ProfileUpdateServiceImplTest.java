package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
//@SpringBootTest
@Slf4j
class ProfileUpdateServiceImplTest {

    @Test
    void create() {
        ProfileUpdate build = ProfileUpdate.builder()
                .nipam("900800456")
                .nama("Jajal")
                .jabatan("Jajal")
                .tableName(EProfileUpdateTable.KELUARGA)
                .actionType(RevisionMetadata.RevisionType.UPDATE)
                .dataDescription(generateDescription(RevisionMetadata.RevisionType.UPDATE, EProfileUpdateTable.KELUARGA))
                .build();
        log.info("build: {}", build);
    }

    private String generateDescription(RevisionMetadata.RevisionType type, EProfileUpdateTable table) {
        String tableDescription = generateTableDescription(table);
        return switch (type) {
            case INSERT -> "Penambahan ";
            case UPDATE -> "Perubahan";
            case DELETE -> "Penghapusan ";
            default -> "Unknown";
        } + " " + tableDescription;
    }

    private String generateTableDescription(EProfileUpdateTable table) {
        return switch (table) {
            case BIODATA -> "data profil";
            case KELUARGA -> "data anggota keluarga";
            case PENDIDIKAN -> "data pendidikan";
            case PENGALAMAN_KERJA -> "data pengalaman kerja";
            case PELATIHAN -> "data pelatihan";
            case KEAHLIAN -> "data keahlian";
        };
    }
}