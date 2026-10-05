package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportCleanupScheduler {
    private final ReportJobService reportJobService;
    private final FileUploadUtil fileUploadUtil;

    // Run every hour by default
    @Scheduled(cron = "${app.report.cleanup.cron:0 0 * * * *}")
    public void cleanupExpiredReports() {
        long twoHoursMillis = 2 * 60 * 60 * 1000L;
        reportJobService.cleanExpiredJobs(twoHoursMillis);
        fileUploadUtil.cleanExpiredLaporanFiles(twoHoursMillis);
    }
}
