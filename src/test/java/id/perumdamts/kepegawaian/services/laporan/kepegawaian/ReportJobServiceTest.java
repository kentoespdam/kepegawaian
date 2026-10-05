package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobStatus;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportJobServiceTest {

    @Test
    void testSubmitAndGetJob() throws Exception {
        FileUploadUtil fileUploadUtil = mock(FileUploadUtil.class);
        ReportJobService service = new ReportJobService(Executors.newSingleThreadExecutor(), fileUploadUtil);

        ReportJobResponse response = service.submitJob("duk", "test.xlsx", () -> "content".getBytes());
        assertNotNull(response.jobId());
        assertTrue(response.status() == ReportJobStatus.PENDING || response.status() == ReportJobStatus.PROCESSING);

        // Wait a bit for async execution
        Thread.sleep(200);

        ReportJobResponse status = service.getJobStatus(response.jobId());
        assertNotNull(status);
    }
}
