package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.time.LocalDateTime;

public record ReportJobResponse(
        String jobId,
        ReportJobStatus status,
        String downloadUrl,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        String errorMessage
) {
}
