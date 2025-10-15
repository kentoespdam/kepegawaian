package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevInfoServiceImpl implements RevInfoService {
    private final EntityManager entityManager;


    @Override
    public ProfilUpdateDetail<ProfilKeluargaResponse> findKeluargaRevision(ProfileUpdate profileUpdate) {
        List<ProfilKeluargaResponse> froms = findLatestRevision(ProfilKeluarga.class, profileUpdate.getRevId()).stream()
                .map(ProfilKeluargaResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, froms);
    }

    @Override
    public ProfilUpdateDetail<PendidikanResponse> findPendidikan(ProfileUpdate profileUpdate) {
        List<PendidikanResponse> result = findLatestRevision(Pendidikan.class, profileUpdate.getRevId()).stream()
                .map(PendidikanResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    private <T> List<T> findLatestRevision(Class<T> entityClass, Long entityId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        @SuppressWarnings("unchecked")
        List<Object[]> result = auditReader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .add(AuditEntity.id().eq(entityId))
                .addOrder(AuditEntity.revisionNumber().desc())
                .setMaxResults(2)
                .getResultList();

        return extractEntities(result);
    }

    /**
     * Type-safe entity extraction
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> extractEntities(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .map(result -> (T) result[0])
                .toList();
    }

}
