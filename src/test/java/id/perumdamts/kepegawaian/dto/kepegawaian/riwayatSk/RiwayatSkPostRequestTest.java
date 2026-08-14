package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression: sebelumnya {@code mkgbTahun}/{@code mkgbBulan} (Integer) salah
 * di-annotate {@code @JsonSerialize(using = LocalDateSerializer.class)} — serialisasi
 * Jackson melempar {@code ClassCastException: Integer cannot be cast to LocalDate}
 * (lihat kegagalan RiwayatTerminasiPostRequestTest saat masih extends DTO ini).
 */
class RiwayatSkPostRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void jacksonSerialize_mkgbIntegerTampilSebagaiAngka_tanpaClassCastException() throws Exception {
        RiwayatSkPostRequest request = new RiwayatSkPostRequest();
        request.setPegawaiId(1L);
        request.setNomorSk("SK-TEST");
        request.setJenisSk(EJenisSk.SK_KENAIKAN_GAJI_BERKALA);
        request.setTanggalSk(LocalDate.of(2026, 8, 14));
        request.setTmtBerlaku(LocalDate.of(2026, 8, 14));
        request.setGolonganId(2L);
        request.setGajiPokok(5_000_000.0);
        request.setMkgTahun(15);
        request.setMkgBulan(6);
        request.setKenaikanBerikutnya(LocalDate.of(2028, 6, 30));
        request.setMkgbTahun(2);
        request.setMkgbBulan(0);

        JsonNode json = objectMapper.valueToTree(request);

        assertThat(json.has("mkgbTahun")).isTrue();
        assertThat(json.has("mkgbBulan")).isTrue();
        assertThat(json.get("mkgbTahun").isInt()).as("mkgbTahun harus angka (Integer)").isTrue();
        assertThat(json.get("mkgbBulan").isInt()).as("mkgbBulan harus angka (Integer)").isTrue();
        assertThat(json.get("mkgbTahun").asInt()).isEqualTo(2);
        assertThat(json.get("mkgbBulan").asInt()).isZero();

        // field tanggal tetap string dengan pola yyyy-MM-dd
        assertThat(json.get("tanggalSk").asText()).isEqualTo("2026-08-14");
    }

    @Test
    void jacksonSerialize_subclassPutJugaAman_karenaMewarisiPerbaikan() throws Exception {
        // RiwayatSkPutRequest adalah subclass kosong — perbaikan mkgb* harus ikut terwarisi
        RiwayatSkPutRequest request = new RiwayatSkPutRequest();
        request.setPegawaiId(1L);
        request.setNomorSk("SK-PUT-TEST");
        request.setJenisSk(EJenisSk.SK_KENAIKAN_GAJI_BERKALA);
        request.setTanggalSk(LocalDate.of(2026, 8, 14));
        request.setTmtBerlaku(LocalDate.of(2026, 8, 14));
        request.setMkgbTahun(2);
        request.setMkgbBulan(0);

        JsonNode json = objectMapper.valueToTree(request);

        assertThat(json.get("mkgbTahun").isInt()).as("mkgbTahun (warisan) harus angka").isTrue();
        assertThat(json.get("mkgbBulan").isInt()).as("mkgbBulan (warisan) harus angka").isTrue();
    }
}
