package id.perumdamts.kepegawaian.services.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.entities.system.PrefPermission;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.PrefPermissionRepository;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrefPermissionService {
    private final PrefRoleRepository roleRepository;
    private final PrefPermissionRepository permissionRepository;

    @Transactional
    public void assign(String roleId, String permName) {
        PrefRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        PrefPermission permission = permissionRepository.findById(permName)
                .orElseThrow(() -> new NotFoundException("Permission tidak ditemukan"));
        if (!role.getPermissions().add(permission)) {
            throw new ConflictException("Permission sudah ter-assign ke role");
        }
        roleRepository.save(role);
    }

    @Transactional
    public void revoke(String roleId, String permName) {
        PrefRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        PrefPermission permission = permissionRepository.findById(permName)
                .orElseThrow(() -> new NotFoundException("Permission tidak ditemukan"));
        if (!role.getPermissions().remove(permission)) {
            throw new NotFoundException("Permission tidak ter-assign ke role");
        }
        roleRepository.save(role);
    }
}
