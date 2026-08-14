package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Swagger/springdoc merender schema request dari properti yang dilihat Jackson.
 * Test ini proxy render tersebut: DTO mutasi dedicated memuat semua field kontrak
 * (BE-REQUIREMENT-form-mutasi §5) — termasuk field SK-gaji yang kondisional per
 * jenisMutasi — dan TIDAK memuat updateMaster/nipam/nama yang tidak dipakai.
 */
class RiwayatMutasiPostRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void jacksonSchema_memuatFieldKontrakMutasi_tanpaUpdateMasterDanNipamNama() throws Exception {
        RiwayatMutasiPostRequest request = new RiwayatMutasiPostRequest();
        request.setPegawaiId(1L);
        request.setNomorSk("SK-MUTASI-TEST");
        request.setJenisSk(EJenisSk.SK_MUTASI);
        request.setTanggalSk(LocalDate.of(2026, 8, 14));
        request.setTmtBerlaku(LocalDate.of(2026, 8, 14));
        request.setGolonganId(2L);
        request.setGajiPokok(5_000_000.0);
        request.setMkgTahun(15);
        request.setMkgBulan(6);
        request.setKenaikanBerikutnya(LocalDate.of(2028, 6, 30));
        request.setMkgbTahun(2);
        request.setMkgbBulan(0);
        request.setNotes("test");
        request.setJenisMutasi(EJenisMutasi.MUTASI_GAJI_BERKALA);
        request.setTanggalBerakhir(LocalDate.of(2026, 8, 14));
        request.setOrganisasiId(3L);
        request.setJabatanId(4L);
        request.setProfesiId(5L);
        request.setOrganisasiLamaId(6L);
        request.setJabatanLamaId(7L);
        request.setGolonganLamaId(8L);
        request.setProfesiLamaId(9L);

        JsonNode json = objectMapper.valueToTree(request);

        // field kontrak mutasi (SK inti + SK-gaji kondisional + mutasi) tetap tampil
        for (String field : new String[]{
                "pegawaiId", "nomorSk", "jenisSk", "tanggalSk", "tmtBerlaku", "notes",
                "golonganId", "gajiPokok", "mkgTahun", "mkgBulan", "kenaikanBerikutnya", "mkgbTahun", "mkgbBulan",
                "jenisMutasi", "tanggalBerakhir",
                "organisasiId", "jabatanId", "profesiId",
                "organisasiLamaId", "jabatanLamaId", "golonganLamaId", "profesiLamaId"}) {
            assertThat(json.has(field)).as("field %s harus tampil", field).isTrue();
        }

        // noise / dead field tidak boleh ada di DTO mutasi
        for (String field : new String[]{"updateMaster", "nipam", "nama"}) {
            assertThat(json.has(field)).as("field %s tidak boleh ada", field).isFalse();
        }
    }
}
