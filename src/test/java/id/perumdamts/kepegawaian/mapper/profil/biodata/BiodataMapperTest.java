package id.perumdamts.kepegawaian.mapper.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression (bd kepegawaian-g2ks): PATCH parsial {"telp": "..."} menghapus
 * nama/alamat/dll jadi NULL karena patchEntity melakukan setter tanpa null-guard.
 * Guard null: field yang tidak dikirim (null) tidak menyentuh nilai lama.
 */
class BiodataMapperTest {

    @Test
    void partialPatchOnlyAppliesSentFields() {
        Biodata entity = new Biodata();
        entity.setNama("Bagus Sudrajat");
        entity.setAlamat("Pajerukan RT 01/01");
        entity.setJenisKelamin(EJenisKelamin.LAKI_LAKI);
        entity.setStatusKawin(EStatusKawin.KAWIN);
        entity.setAgama(EAgama.ISLAM);
        entity.setTempatLahir("Banyumas");
        entity.setTanggalLahir(LocalDate.of(1990, 8, 8));
        entity.setIbuKandung("Haryanti");
        entity.setTelp("123456789321");

        BiodataPatchRequest request = new BiodataPatchRequest();
        request.setTelp("0813-VERIFY-SELF");

        BiodataMapper.patchEntity(entity, request);

        assertEquals("0813-VERIFY-SELF", entity.getTelp());
        assertEquals("Bagus Sudrajat", entity.getNama());
        assertEquals("Pajerukan RT 01/01", entity.getAlamat());
        assertEquals(EJenisKelamin.LAKI_LAKI, entity.getJenisKelamin());
        assertEquals(EStatusKawin.KAWIN, entity.getStatusKawin());
        assertEquals(EAgama.ISLAM, entity.getAgama());
        assertEquals("Banyumas", entity.getTempatLahir());
        assertEquals(LocalDate.of(1990, 8, 8), entity.getTanggalLahir());
        assertEquals("Haryanti", entity.getIbuKandung());
    }

    @Test
    void emptyStringStillClearsField() {
        Biodata entity = new Biodata();
        entity.setTelp("123456789321");

        BiodataPatchRequest request = new BiodataPatchRequest();
        request.setTelp("");

        BiodataMapper.patchEntity(entity, request);

        assertEquals("", entity.getTelp());
    }
}
