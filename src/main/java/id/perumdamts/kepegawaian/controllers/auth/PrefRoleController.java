package id.perumdamts.kepegawaian.controllers.auth;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/prefs/roles")
@RequiredArgsConstructor
public class PrefRoleController {
    private final PrefRoleRepository repository;

    @GetMapping
    public ResponseEntity<?> index() {
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
    public ResponseEntity<?> store(PrefRole role) {
        boolean isExist = repository.existsById(role.getId());
        if (isExist) {
            return CustomResult.save(SavedStatus.build(ESaveStatus.DUPLICATE, "Role sudah ada"));
        }
        repository.save(role);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, "Role berhasil disimpan"));
    }
}
