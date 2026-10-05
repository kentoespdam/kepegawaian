package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
                (byte) 1
        );

        String json = objectMapper.writeValueAsString(duk);
        assertNotNull(json);
        assertTrue(json.contains("Bagus"));
        assertTrue(json.contains("Informatika"));
    }
}
