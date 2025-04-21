package id.perumdamts.kepegawaian.controllers.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleRequest;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/roles")
@RequiredArgsConstructor
public class PrefRoleController {
    private final PrefRoleRepository repository;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject PrefRoleRequest request) {
        Page<PrefRole> result = repository.findAll(request.getSpecification(), request.getPageable());
        return CustomResult.page(result);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<PrefRole> all = repository.findAll();
        return CustomResult.list(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        Specification<PrefRole> specification = (root, query, cb) ->
                cb.like(root.get("id"), "%" + id + "%");
        List<PrefRole> list = repository.findAll(specification);
        return CustomResult.list(list);
    }

    @PreAuthorize("hasRole('SYSTEM')")
    @PostMapping
    public ResponseEntity<?> store(@Valid @RequestBody PrefRole role, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        boolean isExist = repository.existsById(role.getId());
        if (isExist) {
            return CustomResult.save(SavedStatus.build(ESaveStatus.DUPLICATE, "Role sudah ada"));
        }
        repository.save(role);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, "Role berhasil disimpan"));
    }
}
