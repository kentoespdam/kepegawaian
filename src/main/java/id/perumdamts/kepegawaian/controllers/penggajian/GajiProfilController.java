package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiProfil.GajiProfilCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiProfil.GajiProfilQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/profil")
@RequiredArgsConstructor
public class GajiProfilController {
    private final GajiProfilCommandService commandService;
    private final GajiProfilQueryService queryService;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject @Valid GajiProfilIndexQuery request) {
        return CustomResult.page(queryService.findAll(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject @Valid GajiProfilIndexQuery request) {
        return CustomResult.list(queryService.list(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GajiProfilPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiProfilPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}
