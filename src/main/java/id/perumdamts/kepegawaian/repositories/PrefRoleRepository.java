package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrefRoleRepository extends JpaRepository<PrefRole, String>, JpaSpecificationExecutor<PrefRole> {
}
