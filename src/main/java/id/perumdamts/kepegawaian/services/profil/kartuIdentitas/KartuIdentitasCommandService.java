package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPostRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPutRequest;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKitasRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.KartuIdentitasRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KartuIdentitasCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_KARTU_IDENTITAS = "Unknown Kartu Identitas";
    private static final String UNKNOWN_JENIS_KARTU = "Unknown Jenis Kartu";

    private final KartuIdentitasRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenisKitasRepository jenisKitasRepository;
    private final LampiranProfilService lampiranProfilService;
    private final ChangedStatusResolver resolver;

    @Transactional
    public Long create(KartuIdentitasPostRequest request) {
        Biodata biodata = biodataRepository.findById(request.getNik())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisKitas jenisKartu = jenisKitasRepository.findById(request.getJenisKartuId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_KARTU));

        // Native carcass-finder — JpaSpecificationExecutor.findOne() respects
        // @SQLRestriction and would hide soft-deleted rows (see kepegawaian-33s, ADR-0005).
        Optional<KartuIdentitas> existing = repository.findAnyByUniqueKey(
                request.getNik(), request.getJenisKartuId(), request.getNomorKartu());
        KartuIdentitas entity = existing.orElseGet(KartuIdentitas::new);
        entity.setBiodata(biodata);
        entity.setJenisKartu(jenisKartu);
        entity.setNomorKartu(request.getNomorKartu());
        entity.setTanggalExpired(request.getTanggalExpired());
        entity.setTanggalTerima(request.getTanggalTerima());
        entity.setNotes(request.getNotes());
        entity.setIsDeleted(false);
        entity.setChangedStatus(resolver.requiresApproval());
        // NO profileUpdateService — KartuIdentitas not in EProfileUpdateTable
        KartuIdentitas save = repository.save(entity);
        return save.getId();
    }

    @Transactional
    public Long update(Long id, KartuIdentitasPutRequest request) {
        KartuIdentitas entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KARTU_IDENTITAS));
        Biodata biodata = biodataRepository.findById(request.getNik())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisKitas jenisKartu = jenisKitasRepository.findById(request.getJenisKartuId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_KARTU));
        KartuIdentitasPutRequest.toEntity(request, entity, biodata, jenisKartu);
        entity.setChangedStatus(resolver.requiresApproval());
        // NO profileUpdateService — KartuIdentitas not in EProfileUpdateTable
        return repository.save(entity).getId();
    }

    @Transactional
    public void delete(Long id) {
        KartuIdentitas entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KARTU_IDENTITAS));
        entity.setIsDeleted(true);
        entity.setChangedStatus(resolver.requiresApproval());
        repository.save(entity);
        // NO profileUpdateService — KartuIdentitas not in EProfileUpdateTable
        lampiranProfilService.deleteByRefId(EJenisLampiranProfil.KARTU_IDENTITAS, id);
    }

    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.KARTU_IDENTITAS, id);
    }

    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.KARTU_IDENTITAS, id);
    }

    @Transactional
    public Long addLampiran(KartuIdentitasLampiranPostRequest request) {
        if (!repository.existsById(request.getRefId()))
            throw new NotFoundException(UNKNOWN_KARTU_IDENTITAS);
        lampiranProfilService.addLampiran(request);
        return request.getRefId();
    }

    @Transactional
    public void deleteLampiran(Long id) {
        lampiranProfilService.deleteById(id);
    }
}