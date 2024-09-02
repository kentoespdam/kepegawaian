package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiResponse;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatMutasiRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiwayatMutasiServiceImpl implements RiwayatMutasiService {
    private final RiwayatMutasiRepository repository;
    private final RiwayatSkService riwayatSkService;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;

    @Override
    public List<RiwayatMutasiResponse> findAll(RiwayatMutasiRequest request) {
        return repository.findAll(request.getSpecification()).stream()
                .map(RiwayatMutasiResponse::from).toList();
    }

    @Override
    public Page<RiwayatMutasiResponse> findPage(RiwayatMutasiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatMutasiResponse::from);
    }

    @Override
    public RiwayatMutasiResponse findById(Long id) {
        return repository.findById(id).map(RiwayatMutasiResponse::from).orElse(null);
    }

    @Override
    public Page<RiwayatMutasiResponse> findByPegawaiId(Long pegawaiId, RiwayatMutasiRequest request) {
        if (Objects.isNull(request.getSortBy()) || request.getSortBy().isEmpty()) {
            request.setSortBy("id");
            request.setSortDirection("DESC");
        }
        System.out.println(request.getSortBy());
        System.out.println(request.getSortDirection());
        request.setPegawaiId(pegawaiId);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatMutasiResponse::from);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(RiwayatMutasiPostRequest request) {
        try {
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));

            Organisasi lamaOrganisasi = null;
            Jabatan lamaJabatan = null;
            if (request.getOrganisasiLamaId() != null && request.getOrganisasiLamaId() != 0) {
                lamaOrganisasi = organisasiRepository.findById(request.getOrganisasiLamaId())
                        .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
                lamaJabatan = jabatanRepository.findById(request.getJabatanLamaId())
                        .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            }

            boolean exists = repository.exists(request.getSpecificationMutasi());
            if (exists) {
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Riwayat Mutasi is Exists");
            }

            RiwayatSk riwayatSk = riwayatSkService.saveEntity(request);

            RiwayatMutasi entity = RiwayatMutasiPostRequest.toEntity(
                    riwayatSk,
                    request,
                    organisasi,
                    jabatan,
                    lamaOrganisasi,
                    lamaJabatan
            );

            return SavedStatus.build(ESaveStatus.SUCCESS, repository.save(entity));
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }


    @Transactional
    @Override
    public SavedStatus<?> update(Long id, RiwayatMutasiPutRequest request) {
        try {
            RiwayatMutasi riwayatMutasi = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Riwayat Mutasi"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));

            Organisasi lamaOrganisasi = null;
            Jabatan lamaJabatan = null;
            if (request.getOrganisasiLamaId() != null && request.getOrganisasiLamaId() != 0) {
                lamaOrganisasi = organisasiRepository.findById(request.getOrganisasiLamaId())
                        .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
                lamaJabatan = jabatanRepository.findById(request.getJabatanLamaId())
                        .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            }

            RiwayatSk riwayatSk = riwayatSkService.findEntityById(riwayatMutasi.getRiwayatSk().getId());
            if (riwayatSk == null) throw new RuntimeException("Unknown Riwayat Sk");

            RiwayatMutasi entity = RiwayatMutasiPutRequest.toEntity(
                    riwayatMutasi,
                    riwayatSk,
                    request,
                    organisasi,
                    jabatan,
                    lamaOrganisasi,
                    lamaJabatan
            );
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, entity);

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public Boolean delete(Long id) {
        Optional<RiwayatMutasi> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        riwayatSkService.delete(byId.get().getRiwayatSk().getId());
        repository.deleteById(id);
        return true;
    }
}
