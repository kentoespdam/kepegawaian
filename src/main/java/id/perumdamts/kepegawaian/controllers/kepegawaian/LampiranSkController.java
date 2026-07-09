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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kepegawaian/lampiran")
public class LampiranSkController {
    private final LampiranSkCommandService commandService;
    private final LampiranSkQueryService queryService;

    @GetMapping("/list/{ref}/{refId}")
    public ResponseEntity<ListResult<LampiranSkQuery>> getList(@PathVariable EJenisSk ref, @PathVariable Long refId) {
        return CustomResult.list(queryService.getLampiran(ref, refId));
    }

    @GetMapping("/file/{jenis}/{id}")
    public ResponseEntity<?> getFile(@PathVariable EJenisSk jenis, @PathVariable Long id) {
        return queryService.getFileLampiranById(jenis, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @ModelAttribute LampiranSkPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.addLampiran(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/accept")
    public ResponseEntity<SavedResult<Long>> acceptLampiran(@Valid @RequestBody LampiranSkAcceptRequest request) {
        AppwriteUser appwriteUser = (AppwriteUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.acceptLampiran(request, appwriteUser.getName()).getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{ref}/{refId}/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable EJenisSk ref, @PathVariable Long refId, @PathVariable Long id) {
        commandService.deleteLampiran(ref, refId, id);
        return CustomResult.delete(true);
    }
}
