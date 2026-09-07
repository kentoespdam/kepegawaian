package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiListRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiResponse;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiUploadResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiKpi.GajiKpiBatchService;
import id.perumdamts.kepegawaian.services.penggajian.gajiKpi.GajiKpiCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiKpi.GajiKpiQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Penggajian — Gaji KPI")
@RestController
@RequestMapping("/penggajian/kpi")
@RequiredArgsConstructor
public class GajiKpiController {
    private final GajiKpiCommandService commandService;
    private final GajiKpiQueryService queryService;
    private final GajiKpiBatchService batchService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<GajiKpiResponse>>> index(@ParameterObject @Valid GajiKpiIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<GajiKpiResponse>> list(@ParameterObject @Valid GajiKpiListRequest request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "show")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiKpiResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @Operation(summary = "Buat data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody GajiKpiPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiKpiPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:DELETE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @Operation(summary = "Upload batch data Gaji KPI via Excel")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SingleResult<GajiKpiUploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("periode") String periode) {
        String username = getCurrentUsername();
        return CustomResult.any(batchService.upload(file, periode, username));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "Unduh template Excel Gaji KPI")
    @GetMapping("/template/download")
    public ResponseEntity<Resource> downloadTemplate() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Tunkin Template.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(batchService.getTemplateResource());
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getPrincipal() instanceof AppwriteUser user && user.getName() != null && !user.getName().isBlank()) {
                return user.getName();
            }
            if (auth.getName() != null && !auth.getName().isBlank() && !"anonymousUser".equalsIgnoreCase(auth.getName())) {
                return auth.getName();
            }
        }
        return "system";
    }
}
