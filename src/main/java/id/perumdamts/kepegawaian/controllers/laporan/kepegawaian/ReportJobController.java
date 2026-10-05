package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.ReportJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Laporan — Report Jobs (Claim Order)")
@RestController
@RequestMapping("/laporan/kepegawaian/jobs")
@RequiredArgsConstructor
public class ReportJobController {
    private final ReportJobService reportJobService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "Get report job status")
    @GetMapping("/{jobId}")
    public ResponseEntity<SingleResult<ReportJobResponse>> getJobStatus(@PathVariable String jobId) {
        return CustomResult.any(reportJobService.getJobStatus(jobId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "Download completed report file")
    @GetMapping("/{jobId}/download")
    public ResponseEntity<Resource> downloadJobFile(@PathVariable String jobId) {
        Resource resource = reportJobService.getJobFile(jobId);
        long contentLength = -1;
        try {
            contentLength = resource.contentLength();
        } catch (Exception ignored) {}
        var builder = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM);
        if (contentLength >= 0) {
            builder.contentLength(contentLength);
        }
        return builder.body(resource);
    }
}
