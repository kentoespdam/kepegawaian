package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasService;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BiodataServiceImpl implements BiodataService {
    private final BiodataRepository repository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final KartuIdentitasService kartuIdentitasService;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public List<BiodataResponse> findAll() {
        return repository.findAll().stream().map(BiodataResponse::from).toList();
    }

    @Override
    public Page<BiodataResponse> findPage(BiodataRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(BiodataResponse::from);
    }

    @Override
    public BiodataResponse findById(String id) {
        return repository.findById(id).map(BiodataResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(BiodataPostRequest request) {
        Optional<JenjangPendidikan> pendidikanTerakhir = jenjangPendidikanRepository.findById(request.getPendidikanTerakhir());
        if (pendidikanTerakhir.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Pendidikan Terakhir Tidak Valid");

        Biodata entity = BiodataPostRequest.toEntity(request, pendidikanTerakhir.get());
        boolean isBiodataExist = repository.exists(request.getSpecification());
        if (isBiodataExist)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Biodata sudah ada");

        Biodata save = repository.save(entity);
        kartuIdentitasService.execSave(new KartuIdentitas(save));
        return SavedStatus.build(ESaveStatus.SUCCESS, BiodataResponse.from(save));
    }

    @Transactional
    @Override
    public SavedStatus<?> update(String id, BiodataPutRequest request) {
        Optional<JenjangPendidikan> pendidikanTerakhir = jenjangPendidikanRepository.findById(request.getPendidikanTerakhir());
        if (pendidikanTerakhir.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Pendidikan Terakhir Tidak Valid");

        Biodata entity = BiodataPutRequest.toEntity(request, pendidikanTerakhir.get());
        Optional<Biodata> biodata = repository.findById(id);
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        Biodata save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, BiodataResponse.from(save));
    }

    @Override
    public SavedStatus<?> updateFotoProfil(String id, MultipartFile fileName) {
        Optional<Biodata> biodata = repository.findById(id);
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        String oldFilename = biodata.get().getFotoProfil();
        fileUploadUtil.deleteOldFile(oldFilename, EJenisLampiranProfil.FOTO_PROFIL, id);

        UploadResultUtil uploadResultUtil = fileUploadUtil.uploadFile(fileName, EJenisLampiranProfil.FOTO_PROFIL, id);
        if (!uploadResultUtil.isSuccess()) {
            return SavedStatus.build(ESaveStatus.FAILED, uploadResultUtil.getMessage());
        }

        Biodata update = biodata.get();
        update.setFotoProfil(uploadResultUtil.getFileName());
        Biodata save = repository.save(update);
        return SavedStatus.build(ESaveStatus.SUCCESS, BiodataResponse.from(save));
    }

    @Override
    public ResponseEntity<?> findFotoProfil(String id) {
        Optional<Biodata> biodata = repository.findById(id);
        if (biodata.isEmpty() || biodata.get().getFotoProfil() == null || biodata.get().getFotoProfil().isEmpty())
            return ErrorResult.build("Foto Profil Not Found!");

        try {
            Path path = fileUploadUtil.generatePath(EJenisLampiranProfil.FOTO_PROFIL, id, biodata.get().getFotoProfil());
            FileInputStream stream = new FileInputStream(path.toFile());
            String extension = FilenameUtils.getExtension(path.toFile().getName());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", "image/" + extension)
                    .header("Content-Disposition", "inline; filename=\"" + biodata.get().getFotoProfil() + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public Boolean deleteById(String id) {
        boolean isBiodataExist = repository.existsById(id);
        if (!isBiodataExist)
            return false;
        repository.deleteById(id);
        return true;
    }
}
