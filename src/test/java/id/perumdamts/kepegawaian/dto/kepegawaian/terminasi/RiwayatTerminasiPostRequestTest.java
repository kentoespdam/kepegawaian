package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Swagger/springdoc merender schema request dari properti yang dilihat Jackson.
 * Test ini proxy render tersebut: DTO terminasi dedicated hanya memuat field yang
 * dibutuhkan proses save — field SK-gaji (gajiPokok, mkg*, kenaikanBerikutnya,
 * updateMaster) tidak boleh ada sama sekali.
 */
class RiwayatTerminasiPostRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void jacksonSchema_hanyaMemuatFieldTerminasi_tanpaNoiseSkGaji() throws Exception {
        RiwayatTerminasiPostRequest request = new RiwayatTerminasiPostRequest();
        request.setPegawaiId(1L);
        request.setNomorSk("SK-TEST");
        request.setJenisSk(EJenisSk.SK_PENSIUN);
        request.setTanggalSk(LocalDate.of(2026, 8, 14));
        request.setTmtBerlaku(LocalDate.of(2026, 8, 14));
        request.setGolonganId(2L);
        request.setNotes("test");
        request.setAlasanTerminasiId(3L);
        request.setNipam("NIPAM-TEST");
        request.setNama("Nama Test");
        request.setOrganisasiId(4L);
        request.setJabatanId(5L);

        JsonNode json = objectMapper.valueToTree(request);

        // field yang dibutuhkan FE / saga tetap tampil
        for (String field : new String[]{
                "pegawaiId", "nomorSk", "jenisSk", "tanggalSk", "tmtBerlaku", "golonganId", "notes",
                "alasanTerminasiId", "nipam", "nama", "organisasiId", "jabatanId"}) {
            assertThat(json.has(field)).as("field %s harus tampil", field).isTrue();
        }

        // field SK-gaji (grup GajiSk) tidak boleh ada di DTO terminasi sama sekali
        for (String field : new String[]{
                "gajiPokok", "mkgTahun", "mkgBulan", "kenaikanBerikutnya", "mkgbTahun", "mkgbBulan", "updateMaster"}) {
            assertThat(json.has(field)).as("field %s tidak boleh ada", field).isFalse();
        }
    }
}
