package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatMutasiRepository;
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
    private final GenericMutasiService genericMutasiService;

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
            EJenisSk jenisSk = switch (request.getJenisMutasi()) {
                case MUTASI_LOKER -> EJenisSk.SK_MUTASI;
                case MUTASI_GOLONGAN -> EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN;
                case MUTASI_GAJI -> EJenisSk.SK_PENYESUAIAN_GAJI;
                case MUTASI_GAJI_BERKALA -> EJenisSk.SK_KENAIKAN_GAJI_BERKALA;
                case MUTASI_JABATAN -> EJenisSk.SK_JABATAN;
                default -> throw new IllegalStateException("Unexpected value: " + request.getJenisMutasi());
            };
            request.setJenisSk(jenisSk);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
        return genericMutasiService.save(request);
    }


    @Transactional
    @Override
    public SavedStatus<?> update(Long id, RiwayatMutasiPutRequest request) {
        try {
            EJenisSk jenisSk = switch (request.getJenisMutasi()) {
                case MUTASI_LOKER -> EJenisSk.SK_MUTASI;
                case MUTASI_GOLONGAN -> EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN;
                case MUTASI_GAJI -> EJenisSk.SK_PENYESUAIAN_GAJI;
                case MUTASI_GAJI_BERKALA -> EJenisSk.SK_KENAIKAN_GAJI_BERKALA;
                case MUTASI_JABATAN -> EJenisSk.SK_JABATAN;
                default -> throw new IllegalStateException("Unexpected value: " + request.getJenisMutasi());
            };
            request.setJenisSk(jenisSk);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
        return genericMutasiService.update(id, request);
    }

    @Override
    public Boolean delete(Long id) {
        Optional<RiwayatMutasi> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        riwayatSkService.delete(byId.get().getRiwayatSk().getId());
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
