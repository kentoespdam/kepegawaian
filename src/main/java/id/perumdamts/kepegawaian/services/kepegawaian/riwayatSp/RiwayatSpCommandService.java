package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPutRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSpRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisSpRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RiwayatSpCommandService {
    private final RiwayatSpRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final JenisSpRepository jenisSpRepository;
    private final FileUploadUtil fileUploadUtil;

    @Transactional(rollbackFor = Exception.class)
    public RiwayatSp save(RiwayatSpPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) {
            throw new ConflictException("Riwayat SP is Exists");
        }
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
        Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
        Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
        JenisSp jenisSp = jenisSpRepository.findById(request.getJenisSpId())
                .orElseThrow(() -> new NotFoundException("Unknown Jenis SP"));

        RiwayatSp entity = RiwayatSpPostRequest.toEntity(request, jenisSp, pegawai, jabatan, organisasi);
        RiwayatSp entityWithFile = saveFile(entity, request);
        return repository.save(entityWithFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public RiwayatSp update(Long id, RiwayatSpPutRequest request) {
        RiwayatSp riwayatSp = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Riwayat SP"));
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
        Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
        Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
        JenisSp jenisSp = jenisSpRepository.findById(request.getJenisSpId())
                .orElseThrow(() -> new NotFoundException("Unknown Jenis SP"));

        RiwayatSp entity = RiwayatSpPutRequest.toEntity(riwayatSp, request, jenisSp, pegawai, jabatan, organisasi);
        RiwayatSp entityWithFile = saveFile(entity, request);
        return repository.save(entityWithFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        RiwayatSp byId = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat SP not found"));
        byId.setIsDeleted(true);
        repository.save(byId);
    }

    private RiwayatSp saveFile(RiwayatSp entity, RiwayatSpPostRequest request) {
        if (Objects.isNull(request.getFileName())) {
            return entity;
        }
        UploadResultUtil uploadResultUtil = fileUploadUtil.uploadFileSp(request.getFileName(), entity.getJenisSp().getKode());
        if (!uploadResultUtil.isSuccess()) {
            throw new RuntimeException(uploadResultUtil.getMessage());
        }

        entity.setFileName(uploadResultUtil.getFileName());
        entity.setHashedFileName(uploadResultUtil.getHashedFileName());
        entity.setMimeType(uploadResultUtil.getMimeType());

        return entity;
    }
}
