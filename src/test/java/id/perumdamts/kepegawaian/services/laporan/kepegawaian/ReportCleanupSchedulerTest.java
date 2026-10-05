package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ReportCleanupSchedulerTest {

    @Test
    void testCleanupExecution() {
        ReportJobService reportJobService = mock(ReportJobService.class);
        FileUploadUtil fileUploadUtil = mock(FileUploadUtil.class);

        ReportCleanupScheduler scheduler = new ReportCleanupScheduler(reportJobService, fileUploadUtil);
        scheduler.cleanupExpiredReports();

        verify(reportJobService, times(1)).cleanExpiredJobs(anyLong());
        verify(fileUploadUtil, times(1)).cleanExpiredLaporanFiles(anyLong());
    }
}
