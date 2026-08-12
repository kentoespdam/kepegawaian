package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasResponse;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianResponse;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaResponse;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaResponse;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
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
public class RevInfoService {
    private final EntityManager entityManager;


    public ProfilUpdateDetail<ProfilKeluargaResponse> findKeluargaRevision(ProfileUpdate profileUpdate) {
        List<ProfilKeluargaResponse> froms = findLatestRevision(ProfilKeluarga.class, Long.valueOf(profileUpdate.getRevId())).stream()
                .map(ProfilKeluargaResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, froms);
    }

    public ProfilUpdateDetail<PendidikanResponse> findPendidikan(ProfileUpdate profileUpdate) {
        List<PendidikanResponse> result = findLatestRevision(Pendidikan.class, Long.valueOf(profileUpdate.getRevId())).stream()
                .map(PendidikanResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    public ProfilUpdateDetail<BiodataResponse> findBiodataRevision(ProfileUpdate profileUpdate) {
        List<BiodataResponse> result = findLatestRevision(Biodata.class, profileUpdate.getRevId()).stream()
                .map(BiodataResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    public ProfilUpdateDetail<KeahlianResponse> findKeahlianRevision(ProfileUpdate profileUpdate) {
        List<KeahlianResponse> result = findLatestRevision(Keahlian.class, Long.valueOf(profileUpdate.getRevId())).stream()
                .map(KeahlianResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    public ProfilUpdateDetail<PelatihanResponse> findPelatihanRevision(ProfileUpdate profileUpdate) {
        List<PelatihanResponse> result = findLatestRevision(Pelatihan.class, Long.valueOf(profileUpdate.getRevId())).stream()
                .map(PelatihanResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    public ProfilUpdateDetail<PengalamanKerjaResponse> findPengalamanKerjaRevision(ProfileUpdate profileUpdate) {
        List<PengalamanKerjaResponse> result = findLatestRevision(PengalamanKerja.class, Long.valueOf(profileUpdate.getRevId())).stream()
                .map(PengalamanKerjaResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    public ProfilUpdateDetail<KartuIdentitasResponse> findKartuIdentitasRevision(ProfileUpdate profileUpdate) {
        List<KartuIdentitasResponse> result = findLatestRevision(KartuIdentitas.class, Long.valueOf(profileUpdate.getRevId())).stream()
                .map(KartuIdentitasResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    public ProfilUpdateDetail<LampiranProfilResponse> findLampiranRevision(ProfileUpdate profileUpdate) {
        List<LampiranProfilResponse> result = findLatestRevision(LampiranProfil.class, Long.valueOf(profileUpdate.getRevId())).stream()
                .map(LampiranProfilResponse::from).toList();
        return ProfilUpdateDetail.build(profileUpdate, result);
    }

    public <T> List<T> findLatestRevision(Class<T> entityClass, Long entityId) {
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

    public <T> List<T> findLatestRevision(Class<T> entityClass, String entityId) {
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
