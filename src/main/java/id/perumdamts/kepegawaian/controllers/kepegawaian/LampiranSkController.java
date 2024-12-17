package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkAcceptRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kepegawaian/lampiran")
public class LampiranSkController {
    private final LampiranSkService service;

    @GetMapping("/list/{ref}/{refId}")
    public ResponseEntity<?> getList(@PathVariable EJenisSk ref, @PathVariable Long refId) {
        return CustomResult.list(service.getLampiran(ref, refId));
    }

    @GetMapping("/file/{jenis}/{id}")
    public ResponseEntity<?> getFile(@PathVariable EJenisSk jenis, @PathVariable Long id) {
        return service.getFileLampiranById(jenis, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute LampiranSkPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.addLampiran(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/accept")
    public ResponseEntity<?> acceptLampiran(@Valid @RequestBody LampiranSkAcceptRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        AppwriteUser appwriteUser = (AppwriteUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return CustomResult.save(service.acceptLampiran(request, appwriteUser.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{ref}/{refId}/{id}")
    public ResponseEntity<?> delete(@PathVariable EJenisSk ref, @PathVariable Long refId, @PathVariable Long id) {
        return CustomResult.delete(service.deleteLampiran(ref, refId, id));
    }
}
