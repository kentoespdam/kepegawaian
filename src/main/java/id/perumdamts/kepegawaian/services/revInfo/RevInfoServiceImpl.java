package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RevInfoServiceImpl implements RevInfoService {
    private final BiodataRepository biodataRepository;
    private final KeahlianRepository keahlianRepository;
    private final LampiranProfilRepository lampiranProfilRepository;
    private final PelatihanRepository pelatihanRepository;
    private final PendidikanRepository pendidikanRepository;
    private final PengalamanKerjaRepository pengalamanKerjaRepository;
    private final ProfilKeluargaRepository profilKeluargaRepository;


    @Override
    public ProfilUpdateDetail findKeluargaRevision(ProfileUpdate profileUpdate) {
        Optional<ProfilKeluarga> byId = profilKeluargaRepository.findByIdAndChangedStatus(profileUpdate.getId(), Boolean.TRUE);
        if (byId.isEmpty())
            return null;
        Optional<Revision<Integer, ProfilKeluarga>> revision = profilKeluargaRepository.findRevision(byId.get().getId(), byId.get().getVersion() - 1);
        if (revision.isEmpty())
            return null;
        ProfilUpdateDetail result = ProfilUpdateDetail.from(profileUpdate);
        result.setNewData(byId.get());
        result.setActionType(revision.get().getMetadata().getRevisionType());
        result.setOldData(revision.get().getEntity());
        return result;
    }
}
