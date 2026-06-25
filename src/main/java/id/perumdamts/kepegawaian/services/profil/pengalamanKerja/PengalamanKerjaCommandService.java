package id.perumdamts.kepegawaian.services.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja.PengalamanKerjaMapper;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPutRequest;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanLampiranPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PengalamanKerjaRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PengalamanKerjaCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PENGALAMAN_KERJA = "Unknown Pengalaman Kerja";

    private final PengalamanKerjaRepository repository;
    private final BiodataRepository biodataRepository;
    private final LampiranProfilService lampiranProfilService;
    private final ProfileUpdateService profileUpdateService;
    private final ChangedStatusResolver resolver;

    @Transactional
    public Long create(PengalamanKerjaPostRequest request) {
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        PengalamanKerja entity = PengalamanKerjaMapper.toEntity(request, biodata);
        entity.setChangedStatus(resolver.requiresApproval());
        PengalamanKerja save = repository.save(entity);
        profileUpdateService.create(save.getId(), RevisionMetadata.RevisionType.INSERT, EProfileUpdateTable.PENGALAMAN_KERJA);
        return save.getId();
    }

    @Transactional
    public Long update(Long id, PengalamanKerjaPutRequest request) {
        PengalamanKerja entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PENGALAMAN_KERJA));
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        PengalamanKerja updated = PengalamanKerjaMapper.updateEntity(entity, request, biodata);
        updated.setChangedStatus(resolver.requiresApproval());
        PengalamanKerja save = repository.save(updated);
        profileUpdateService.create(save.getId(), RevisionMetadata.RevisionType.UPDATE, EProfileUpdateTable.PENGALAMAN_KERJA);
        return save.getId();
    }

    @Transactional
    public void delete(Long id) {
        PengalamanKerja entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PENGALAMAN_KERJA));
        entity.setIsDeleted(true);
        entity.setChangedStatus(resolver.requiresApproval());
        repository.save(entity);
        profileUpdateService.create(entity.getId(), RevisionMetadata.RevisionType.DELETE, EProfileUpdateTable.PENGALAMAN_KERJA);
        lampiranProfilService.deleteByRefId(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
    }

    // Lampiran delegates

    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
    }

    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
    }

    @Transactional
    public Long addLampiran(PengalamanLampiranPostRequest request) {
        if (!repository.existsById(request.getRefId())) {
            throw new NotFoundException(UNKNOWN_PENGALAMAN_KERJA);
        }
        lampiranProfilService.addLampiran(request);
        return request.getRefId();
    }

    public void deleteLampiran(Long id) {
        lampiranProfilService.deleteById(id);
    }
}
