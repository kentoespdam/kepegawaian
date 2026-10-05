package id.perumdamts.kepegawaian.services.system.roles;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleRequest;
import id.perumdamts.kepegawaian.repositories.system.jooq.PrefRoleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrefRoleQueryService {
  private final PrefRoleQueryRepository queryRepository;

  public Page<PrefRole> findPage(PrefRoleRequest request) {
    return queryRepository.findPage(request);
  }
}
