package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.repositories.profil.ProfilKeluargaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncExistProfilKeluarga {
    private final ProfilKeluargaRepository repository;

    @Transactional
    public void syncAudit() {
        Pageable pageable = PageRequest.of(0, 10);
        for (ProfilKeluarga profilKeluarga : repository.findAll()) {
            if (repository.findLastChangeRevision(profilKeluarga.getId()).isEmpty()) {
                profilKeluarga.setChangedStatus(Boolean.TRUE);
                repository.save(profilKeluarga);
            }
        }
    }

//    public List<?> getProfilKeluargaHistory(Long id) {
//        return auditReader.createQuery()
//                .forRevisionsOfEntity(ProfilKeluarga.class, true, true)
//                .add(AuditEntity.id().eq(id))
//                .getResultList();
//    }
}
