package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevInfoServiceImpl implements RevInfoService {
    private final BiodataRepository biodataRepository;
    private final KeahlianRepository keahlianRepository;
    private final LampiranProfilRepository lampiranProfilRepository;
    private final PelatihanRepository pelatihanRepository;
    private final PendidikanRepository pendidikanRepository;
    private final PengalamanKerjaRepository pengalamanKerjaRepository;
    private final ProfilKeluargaRepository profilKeluargaRepository;


    @Override
    public Optional<ProfilUpdateDetail> findKeluargaRevision(ProfileUpdate profileUpdate) {
        return profilKeluargaRepository
                .findByIdAndChangedStatus(profileUpdate.getRevId(), Boolean.TRUE)
                .flatMap(currentEntity -> {
                    log.info("currentEntity Id: {}", currentEntity.getId());
                    return getRevisionData(profileUpdate, currentEntity);
                });
    }

    private Optional<ProfilUpdateDetail> getRevisionData(ProfileUpdate profileUpdate,
                                                         ProfilKeluarga currentEntity) {
        try {
            Long entityId = currentEntity.getId();
            Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
            Page<Revision<Integer, ProfilKeluarga>> revisions = profilKeluargaRepository.findRevisions(currentEntity.getId(), pageable);

//            return Optional.of(buildProfilUpdateDetail(profileUpdate, revisions));

//            return profilKeluargaRepository.findRevision(entityId, previousVersion)
//                    .map(previousRevision -> {
//                        log.info("profilUpdate Id: {}, currentEntity Id: {}, previousRevisionId: {}",
//                                profileUpdate.getId(), currentEntity.getId(), previousRevision.getEntity().getId());
//                        return buildProfilUpdateDetail(
//                                profileUpdate, currentEntity, previousRevision);
//                    });
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error getting revision data for keluarga id {}: {}",
                    currentEntity.getId(), e.getMessage(), e);
            return Optional.empty();
        }
    }

//    private ProfilUpdateDetail buildProfilUpdateDetail(ProfileUpdate profileUpdate,
//                                                       Page<Revision<Integer, ProfilKeluarga>> revisions) {
//        List<Revision<Integer, ProfilKeluarga>> content = revisions.getContent();
//
//        ProfilUpdateDetail detail = ProfilUpdateDetail.from(profileUpdate);
//        detail.setNewData(content.getFirst().getEntity());
//        detail.setActionType(content.getFirst().getMetadata().getRevisionType());
//        detail.setOldData(content.getLast().getEntity());
//
//        log.debug("Successfully built profile update detail for id: {}", profileUpdate.getId());
//        return detail;
//    }


}
