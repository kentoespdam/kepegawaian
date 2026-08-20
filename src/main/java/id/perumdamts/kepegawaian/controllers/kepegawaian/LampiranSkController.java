package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkAcceptRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequiredArgsConstructor
@Tag(name = "Kepegawaian — Lampiran Sk")
@RestController
@RequestMapping("/kepegawaian/lampiran")
public class LampiranSkController {
    private final LampiranSkCommandService commandService;
    private final LampiranSkQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "Ambil list")
    @GetMapping("/list/{ref}/{refId}")
    public ResponseEntity<ListResult<LampiranSkQuery>> getList(@PathVariable EJenisSk ref, @PathVariable Long refId) {
        return CustomResult.list(queryService.getLampiran(ref, refId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "Ambil file")
    @GetMapping("/file/{jenis}/{id}")
    public ResponseEntity<?> getFile(@PathVariable EJenisSk jenis, @PathVariable Long id) {
        return queryService.getFileLampiranById(jenis, id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "Buat data baru")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<Long>> create(@Valid @ModelAttribute LampiranSkPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.addLampiran(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "accept lampiran")
    @PostMapping("/accept")
    public ResponseEntity<SavedResult<Long>> acceptLampiran(@Valid @RequestBody LampiranSkAcceptRequest request) {
        AppwriteUser appwriteUser = (AppwriteUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.acceptLampiran(request, appwriteUser.getName()).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:DELETE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{ref}/{refId}/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable EJenisSk ref, @PathVariable Long refId, @PathVariable Long id) {
        return CustomResult.delete(commandService.deleteLampiran(ref, refId, id));
    }
}
