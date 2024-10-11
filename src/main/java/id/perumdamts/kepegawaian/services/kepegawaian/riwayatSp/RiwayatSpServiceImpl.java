package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpResponse;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.riwayatSp.RiwayatSpRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiwayatSpServiceImpl implements RiwayatSpService {
    private final RiwayatSpRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public Page<RiwayatSpResponse> findPage(Long id, RiwayatSpRequest request) {
        request.setPegawaiId(id);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatSpResponse::from);
    }

    @Override
    public RiwayatSpResponse findById(Long id) {
        return repository.findById(id).map(RiwayatSpResponse::from)
                .orElse(null);
    }

    @Override
    public ResponseEntity<?> getFile(Long id) {
        Optional<RiwayatSp> byId = repository.findById(id);
        if (byId.isEmpty()) return ErrorResult.build("Riwayat SP not found");
        if (Objects.isNull(byId.get().getFileName()))
            return ErrorResult.build("File not found");
        try {
            Path path = fileUploadUtil.generatePathSp(String.valueOf(byId.get().getJenisSp()), byId.get().getHashedFileName());
            FileInputStream stream = new FileInputStream(path.toFile());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", byId.get().getMimeType())
                    .header("Content-Disposition", "inline; filename=\"" + byId.get().getFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ErrorResult.build("File not found");
        }

    }

    @Override
    public SavedStatus<?> save(RiwayatSpPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) throw new RuntimeException("Riwayat SP is Exists");
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId()).orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId()).orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            RiwayatSp entity = RiwayatSpPostRequest.toEntity(request, pegawai, jabatan, organisasi);
            RiwayatSp entity1 = saveFile(entity, request);
            RiwayatSp save = repository.save(entity1);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatSpPutRequest request) {
        try {
            Optional<RiwayatSp> riwayatSp = repository.findById(id);
            if (riwayatSp.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Riwayat SP");
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId()).orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId()).orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            RiwayatSp entity = RiwayatSpPutRequest.toEntity(riwayatSp.get(), request, pegawai, jabatan, organisasi);
            RiwayatSp entity1 = saveFile(entity, request);
            RiwayatSp save = repository.save(entity1);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean delete(Long id) {
        Optional<RiwayatSp> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    private RiwayatSp saveFile(RiwayatSp entity, RiwayatSpPostRequest request) {
        if (Objects.isNull(request.getFileName()))
            return entity;
        UploadResultUtil uploadResultUtil = fileUploadUtil.uploadFileSp(request.getFileName(), String.valueOf(request.getJenisSp()));
        if (!uploadResultUtil.isSuccess())
            throw new RuntimeException(uploadResultUtil.getMessage());

        entity.setFileName(uploadResultUtil.getFileName());
        entity.setHashedFileName(uploadResultUtil.getHashedFileName());
        entity.setMimeType(uploadResultUtil.getMimeType());

        return entity;
    }
}
