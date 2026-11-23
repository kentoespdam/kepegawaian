package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LaporanJsonParityTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void testReportJobResponseSerialization() throws Exception {
        ReportJobResponse res = new ReportJobResponse(
                "123-abc",
                ReportJobStatus.COMPLETED,
                "/laporan/kepegawaian/jobs/123-abc/download",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        String json = objectMapper.writeValueAsString(res);
        assertNotNull(json);
        assertTrue(json.contains("123-abc"));
        assertTrue(json.contains("COMPLETED"));

        ReportJobResponse deserialized = objectMapper.readValue(json, ReportJobResponse.class);
        assertEquals(res.jobId(), deserialized.jobId());
        assertEquals(res.status(), deserialized.status());
    }

    @Test
    void testDukResponseSerialization() throws Exception {
        DukResponse duk = new DukResponse(
                "Bagus",
                "12345",
                "III/a",
                "Penata Muda",
                LocalDate.of(2020, 1, 1),
                "Staff",
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2015, 1, 1),
                11,
                8,
                30,
                "Informatika",
                2015,
                "S1",
                "Calon Pegawai"
        );

        String json = objectMapper.writeValueAsString(duk);
        assertNotNull(json);
        assertTrue(json.contains("Bagus"));
        assertTrue(json.contains("Informatika"));
        assertTrue(json.contains("01.01.2020"));
        assertTrue(json.contains("01.01.2015"));
        assertTrue(json.contains("Calon Pegawai"));
    }

    @Test
    void testDnpResponseSerialization() throws Exception {
        DnpResponse dnp = new DnpResponse(
                "ORG01",
                "GROUP-A",
                3,
                "Budi",
                "8903002",
                "Manager",
                LocalDate.of(2021, 6, 1),
                "Pembina",
                "IV/a",
                LocalDate.of(2020, 1, 1),
                6,
                72,
                LocalDate.of(2015, 3, 15),
                11,
                133,
                "S1 Teknik Informatika 2014",
                "Jakarta 01.01.1990"
        );

        String json = objectMapper.writeValueAsString(dnp);
        assertNotNull(json);
        assertTrue(json.contains("01.06.2021"));
        assertTrue(json.contains("01.01.2020"));
        assertTrue(json.contains("15.03.2015"));
        assertTrue(json.contains("Budi"));
        assertTrue(json.contains("ORG01"));
    }
}
