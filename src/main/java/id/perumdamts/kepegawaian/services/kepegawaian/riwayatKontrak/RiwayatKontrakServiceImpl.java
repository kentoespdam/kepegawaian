package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatKontrakRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiwayatKontrakServiceImpl implements RiwayatKontrakService {
    private final RiwayatKontrakRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final GenericKontrakService genericKontrakService;
    private final LampiranSkService lampiranSkService;


    @Override
    public Page<RiwayatKontrakResponse> findByPegawaiId(Long id, RiwayatKontrakRequest request) {
        if (Objects.isNull(request.getSortBy()) || request.getSortBy().isEmpty()) {
            request.setSortBy("id");
            request.setSortDirection("DESC");
        }
        request.setPegawaiId(id);
        log.info("request: {}", request);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatKontrakResponse::from);
    }

    @Override
    public RiwayatKontrakResponse findById(Long id) {
        return repository.findById(id).map(RiwayatKontrakResponse::from)
                .orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(RiwayatKontrakPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Riwayat Kontrak sudah ada");
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            if (!pegawai.getStatusPegawai().equals(EStatusPegawai.KONTRAK))
                throw new RuntimeException("Pegawai bukan Kontrak");
            RiwayatKontrak save = genericKontrakService.save(request, pegawai);
            if (request.getFileName() != null) {
                LampiranSkPostRequest lampiranRequest = LampiranSkPostRequest.builder()
                        .ref(request.getJenisKontrak().equals(EJenisKontrak.PERPANJANGAN) ? EJenisSk.SK_LAINNYA : EJenisSk.SK_CAPEG)
                        .refId(save.getId())
                        .fileName(request.getFileName())
                        .build();
                lampiranSkService.addLampiran(lampiranRequest);
            }
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, RiwayatKontrakPutRequest request) {
        try {
            RiwayatKontrak riwayatKontrak = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Riwayat Kontrak"));
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            RiwayatKontrak update = genericKontrakService.update(riwayatKontrak, request, pegawai);
            if (request.getFileName() != null) {
                lampiranSkService.deleteByRefId(id);
                LampiranSkPostRequest lampiranRequest = LampiranSkPostRequest.builder()
                        .ref(request.getJenisKontrak().equals(EJenisKontrak.PERPANJANGAN) ? EJenisSk.SK_LAINNYA : EJenisSk.SK_CAPEG)
                        .refId(update.getId())
                        .fileName(request.getFileName())
                        .build();
                lampiranSkService.addLampiran(lampiranRequest);
            }
            return SavedStatus.build(ESaveStatus.SUCCESS, update);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean delete(Long id) {
        Optional<RiwayatKontrak> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        genericKontrakService.delete(byId.get());
        return true;
    }
}
