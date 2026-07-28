package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.profil.biodata.BiodataMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasCommandService;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanCommandService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BiodataCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_JENJANG = "Unknown Jenjang Pendidikan";

    private final BiodataRepository repository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final PendidikanCommandService pendidikanCommandService;
    private final KartuIdentitasCommandService kartuIdentitasCommandService;
    private final ProfileUpdateService profileUpdateService;
    private final FileUploadUtil fileUploadUtil;

    @Transactional
    public String create(BiodataPostRequest request) {
        JenjangPendidikan jenjang = null;
        if (request.getPendidikanTerakhirId() != null) {
            jenjang = jenjangPendidikanRepository.findById(request.getPendidikanTerakhirId())
                    .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
        }

        Biodata entity = BiodataMapper.toEntity(request, jenjang);
        entity = repository.save(entity);

        // Seed: 1 Pendidikan (isLatest=true, changedStatus=false)
        if (jenjang != null) {
            pendidikanCommandService.seedFromBiodata(entity, jenjang);
        }

        // Seed: 1 KartuIdentitas default (KTP, changedStatus=false)
        kartuIdentitasCommandService.seedFromBiodata(entity);

        return entity.getNik();
    }

    @Transactional
    public String update(String nik, BiodataPutRequest request) {
        Biodata entity = repository.findById(nik)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        JenjangPendidikan jenjang = null;
        if (request.getPendidikanTerakhirId() != null) {
            jenjang = jenjangPendidikanRepository.findById(request.getPendidikanTerakhirId())
                    .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
        }

        BiodataMapper.updateEntity(entity, request, jenjang);
        repository.save(entity);
        return entity.getNik();
    }

    @Transactional
    public String patchBiodata(String nik, BiodataPatchRequest request) {
        Biodata entity = repository.findById(nik)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        BiodataMapper.patchEntity(entity, request);
        entity.setChangedStatus(true);
        repository.save(entity);
        profileUpdateService.create(nik, RevisionMetadata.RevisionType.UPDATE, EProfileUpdateTable.BIODATA);
        return entity.getNik();
    }

    @Transactional
    public boolean deleteById(String nik) {
        Biodata entity = repository.findById(nik)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        repository.delete(entity);
        return true;
    }

    // Used by PegawaiServiceImpl.save() when a new Pegawai references a NIK
    // that doesn't yet have a Biodata row. Seeds Pendidikan + KartuIdentitas too.
    @Transactional
    public Biodata saveFromPegawai(BiodataPostRequest request) {
        JenjangPendidikan jenjang = jenjangPendidikanRepository
                .findById(request.getPendidikanTerakhirId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
        Biodata entity = BiodataMapper.toEntity(request, jenjang);
        Biodata saved = repository.save(entity);
        pendidikanCommandService.seedFromBiodata(saved, jenjang);
        kartuIdentitasCommandService.seedFromBiodata(saved);
        return saved;
    }

    public String updateFotoProfil(String id, MultipartFile file) {
        Biodata biodata = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        String oldFilename = biodata.getFotoProfil();
        fileUploadUtil.deleteOldFile(oldFilename, EJenisLampiranProfil.FOTO_PROFIL, id);

        UploadResultUtil result = fileUploadUtil.uploadFileSp(file, EJenisLampiranProfil.FOTO_PROFIL, id);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }

        biodata.setFotoProfil(result.getFileName());
        repository.save(biodata);
        return biodata.getNik();
    }
}
