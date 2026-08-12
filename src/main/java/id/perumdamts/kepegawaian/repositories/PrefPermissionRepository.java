package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.entities.system.PrefPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrefPermissionRepository extends JpaRepository<PrefPermission, String> {
}
