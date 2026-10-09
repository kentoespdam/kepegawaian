package id.perumdamts.kepegawaian.services.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.enums.AppPermission;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrefPermissionService {
    private final PrefRoleRepository roleRepository;

    @Transactional
    public void assign(String roleId, String permName) {
        PrefRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        AppPermission permission = Arrays.stream(AppPermission.values())
                .filter(p -> p.getAuthority().equals(permName))
                .findFirst()
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
        AppPermission permission = Arrays.stream(AppPermission.values())
                .filter(p -> p.getAuthority().equals(permName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Permission tidak ditemukan"));
        if (!role.getPermissions().remove(permission)) {
            throw new NotFoundException("Permission tidak ter-assign ke role");
        }
        roleRepository.save(role);
    }
}
