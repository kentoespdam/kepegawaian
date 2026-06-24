package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasCommandService;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanCommandService;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class BiodataCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_JENJANG = "Unknown Jenjang Pendidikan";

    private final BiodataRepository repository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final PendidikanCommandService pendidikanCommandService;
    private final KartuIdentitasCommandService kartuIdentitasCommandService;
    private final FileUploadUtil fileUploadUtil;

    @Transactional
    public Biodata create(BiodataPostRequest request) {
        JenjangPendidikan jenjang = null;
        if (request.getPendidikanTerakhirId() != null) {
            jenjang = jenjangPendidikanRepository.findById(request.getPendidikanTerakhirId())
                    .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
        }

        Biodata entity = BiodataPostRequest.toEntity(request, jenjang);
        entity = repository.save(entity);

        // Seed: 1 Pendidikan (isLatest=true, changedStatus=false)
        if (jenjang != null) {
            pendidikanCommandService.seedFromBiodata(entity, jenjang);
        }

        // Seed: 1 KartuIdentitas default (KTP, changedStatus=false)
        kartuIdentitasCommandService.seedFromBiodata(entity);

        return entity;
    }

    @Transactional
    public Biodata update(String nik, BiodataPutRequest request) {
        Biodata entity = repository.findById(nik)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        JenjangPendidikan jenjang = null;
        if (request.getPendidikanTerakhirId() != null) {
            jenjang = jenjangPendidikanRepository.findById(request.getPendidikanTerakhirId())
                    .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
        }

        BiodataPutRequest.toEntity(request, entity, jenjang);
        return repository.save(entity);
    }

    @Transactional
    public Biodata patchBiodata(String nik, BiodataPatchRequest request) {
        Biodata entity = repository.findById(nik)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        BiodataPatchRequest.toEntity(entity, request);
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(String nik) {
        Biodata entity = repository.findById(nik)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        repository.delete(entity);
    }

    // Used by PegawaiServiceImpl.save() when a new Pegawai references a NIK
    // that doesn't yet have a Biodata row. Seeds Pendidikan + KartuIdentitas too.
    @Transactional
    public Biodata saveFromPegawai(BiodataPostRequest request) {
        JenjangPendidikan jenjang = jenjangPendidikanRepository
                .findById(request.getPendidikanTerakhirId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
        Biodata entity = BiodataPostRequest.toEntity(request, jenjang);
        Biodata saved = repository.save(entity);
        pendidikanCommandService.seedFromBiodata(saved, jenjang);
        kartuIdentitasCommandService.seedFromBiodata(saved);
        return saved;
    }

    public Biodata updateFotoProfil(String id, MultipartFile file) {
        Biodata biodata = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        String oldFilename = biodata.getFotoProfil();
        fileUploadUtil.deleteOldFile(oldFilename, EJenisLampiranProfil.FOTO_PROFIL, id);

        UploadResultUtil result = fileUploadUtil.uploadFileSp(file, EJenisLampiranProfil.FOTO_PROFIL, id);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }

        biodata.setFotoProfil(result.getFileName());
        return repository.save(biodata);
    }

    public ResponseEntity<?> findFotoProfil(String id) {
        Biodata biodata = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        if (biodata.getFotoProfil() == null || biodata.getFotoProfil().isEmpty()) {
            return ErrorResult.build("Foto Profil Not Found!");
        }

        try {
            Path path = fileUploadUtil.generatePath(EJenisLampiranProfil.FOTO_PROFIL, id, biodata.getFotoProfil());
            FileInputStream stream = new FileInputStream(path.toFile());
            String extension = FilenameUtils.getExtension(path.toFile().getName());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", "image/" + extension)
                    .header("Content-Disposition", "inline; filename=\"" + biodata.getFotoProfil() + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
