package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.mapper.profil.pendidikan.PendidikanMapper;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PendidikanRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PendidikanCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PENDIDIKAN = "Unknown Pendidikan";
    private static final String UNKNOWN_JENJANG = "Unknown Jenjang Pendidikan";

    private final PendidikanRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final LampiranProfilCommandService lampiranProfilCommandService;
    private final ProfileUpdateService profileUpdateService;
    private final ChangedStatusResolver resolver;

    @Transactional
    public Long create(PendidikanPostRequest request) {
        Biodata biodata = biodataRepository.findById(request.getBiodataId()).orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenjangPendidikan jenjangPendidikan = jenjangPendidikanRepository.findById(request.getJenjangPendidikanId()).orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));

        Optional<Pendidikan> existing = repository.findAnyByUniqueKey(biodata.getNik(), jenjangPendidikan.getId(), request.getTahunMasuk());
        Pendidikan pendidikan = existing.orElseGet(Pendidikan::new);
        pendidikan.setBiodata(biodata);
        pendidikan.setJenjangPendidikan(jenjangPendidikan);
        pendidikan.setGelarDepan(request.getGelarDepan());
        pendidikan.setGelarBelakang(request.getGelarBelakang());
        pendidikan.setJurusan(request.getJurusan());
        pendidikan.setInstitusi(request.getInstitusi());
        pendidikan.setKota(request.getKota());
        pendidikan.setTahunMasuk(request.getTahunMasuk());
        pendidikan.setIsLulus(request.getIsLulus());
        pendidikan.setTahunLulus(request.getTahunLulus());
        pendidikan.setGpa(request.getGpa());
        pendidikan.setIsLatest(request.getIsLatest());
        pendidikan.setIsDeleted(false);
        pendidikan.setChangedStatus(resolver.requiresApproval());

        Pendidikan save = repository.save(pendidikan);
        handleUpdateIsLatest(request.getIsLatest(), save.getId(), biodata, jenjangPendidikan);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.INSERT);
        return save.getId();
    }

    @Transactional
    public Long update(Long id, PendidikanPutRequest request) {
        Pendidikan pendidikan = repository.findById(id).orElseThrow(() -> new NotFoundException(UNKNOWN_PENDIDIKAN));
        Biodata biodata = biodataRepository.findById(request.getBiodataId()).orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenjangPendidikan jenjangPendidikan = jenjangPendidikanRepository.findById(request.getJenjangPendidikanId()).orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));

        Pendidikan entity = PendidikanMapper.updateEntity(pendidikan, request, biodata, jenjangPendidikan);
        entity.setChangedStatus(resolver.requiresApproval());

        Pendidikan save = repository.save(entity);
        handleUpdateIsLatest(request.getIsLatest(), save.getId(), biodata, jenjangPendidikan);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.UPDATE);
        return save.getId();
    }

    @Transactional
    public void delete(Long id) {
        Pendidikan entity = repository.findById(id).orElseThrow(() -> new NotFoundException(UNKNOWN_PENDIDIKAN));
        entity.setIsDeleted(true);
        entity.setChangedStatus(resolver.requiresApproval());
        repository.save(entity);

        handleRevisionUpdate(entity, RevisionMetadata.RevisionType.DELETE);
        lampiranProfilCommandService.deleteByRefId(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }

    @Transactional
    public Pendidikan seedFromBiodata(Biodata biodata, JenjangPendidikan jenjang) {
        Pendidikan entity = new Pendidikan();
        entity.setBiodata(biodata);
        entity.setJenjangPendidikan(jenjang);
        entity.setIsLatest(true);
        entity.setChangedStatus(false);
        Pendidikan saved = repository.save(entity);
        handleUpdateIsLatest(true, saved.getId(), biodata, jenjang);
        return saved;
    }

    private void handleUpdateIsLatest(Boolean isLatest, Long id, Biodata biodata, JenjangPendidikan jenjangPendidikan) {
        if (Boolean.FALSE.equals(isLatest)) return;
        repository.updateIsLatest(id, biodata.getNik());
        biodata.setPendidikanTerakhir(jenjangPendidikan);
        biodataRepository.save(biodata);
    }

    private void handleRevisionUpdate(Pendidikan save, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(save.getChangedStatus())) return;
        profileUpdateService.create(save.getId(), type, EProfileUpdateTable.PENDIDIKAN);
    }
}
