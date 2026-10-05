package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobStatus;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Service
public class ReportJobService {
    private final Executor taskExecutor;
    private final FileUploadUtil fileUploadUtil;

    public ReportJobService(@Qualifier("taskExecutor") Executor taskExecutor, FileUploadUtil fileUploadUtil) {
        this.taskExecutor = taskExecutor;
        this.fileUploadUtil = fileUploadUtil;
    }

    public static class JobDetail {
        private final String jobId;
        private final String modul;
        private final String fileName;
        private ReportJobStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String errorMessage;

        public JobDetail(String jobId, String modul, String fileName) {
            this.jobId = jobId;
            this.modul = modul;
            this.fileName = fileName;
            this.status = ReportJobStatus.PENDING;
            this.createdAt = LocalDateTime.now();
        }

        public ReportJobResponse toResponse() {
            String downloadUrl = status == ReportJobStatus.COMPLETED ?
                    "/laporan/kepegawaian/jobs/" + jobId + "/download" : null;
            return new ReportJobResponse(jobId, status, downloadUrl, createdAt, completedAt, errorMessage);
        }
    }

    private final Map<String, JobDetail> jobs = new ConcurrentHashMap<>();

    public ReportJobResponse submitJob(String modul, String fileName, Supplier<byte[]> generator) {
        String jobId = UUID.randomUUID().toString();
        JobDetail detail = new JobDetail(jobId, modul, fileName);
        jobs.put(jobId, detail);

        CompletableFuture.runAsync(() -> {
            detail.status = ReportJobStatus.PROCESSING;
            try {
                byte[] data = generator.get();
                fileUploadUtil.saveFileLaporan(data, "temp", fileName);
                detail.status = ReportJobStatus.COMPLETED;
                detail.completedAt = LocalDateTime.now();
            } catch (Exception e) {
                detail.status = ReportJobStatus.FAILED;
                detail.errorMessage = e.getMessage();
                detail.completedAt = LocalDateTime.now();
            }
        }, taskExecutor);

        return detail.toResponse();
    }

    public ReportJobResponse getJobStatus(String jobId) {
        JobDetail detail = jobs.get(jobId);
        if (detail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found: " + jobId);
        }
        return detail.toResponse();
    }

    public Resource getJobFile(String jobId) {
        JobDetail detail = jobs.get(jobId);
        if (detail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found: " + jobId);
        }
        if (detail.status != ReportJobStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job not completed yet: " + detail.status);
        }
        Resource resource = fileUploadUtil.loadFileLaporanAsResource("temp", detail.fileName);
        if (resource == null || !resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report file not found on storage");
        }
        return resource;
    }

    public void cleanExpiredJobs(long maxAgeMillis) {
        long now = System.currentTimeMillis();
        jobs.entrySet().removeIf(entry -> {
            JobDetail d = entry.getValue();
            if (d.createdAt != null) {
                long age = now - java.sql.Timestamp.valueOf(d.createdAt).getTime();
                if (age > maxAgeMillis) {
                    fileUploadUtil.deleteOldFileLaporan("temp", d.fileName);
                    return true;
                }
            }
            return false;
        });
    }
}
