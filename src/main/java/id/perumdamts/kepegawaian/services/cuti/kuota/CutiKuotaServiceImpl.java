package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.kuota.*;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CutiKuotaServiceImpl implements CutiKuotaService {
    private final CutiKuotaRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final ProcessCutiKuotaService processCutiKuotaService;

    @Override
    public CutiKuotaPegawaiResponse findPage(CutiKuotaRequest request) {
        Page<CutiKuotaResponse> page = repository.findAll(request.getSpecification(), request.getPageable()).map(CutiKuotaResponse::from);
        if (page.isEmpty()) return null;
        List<Long> pegawaiIdList = page.getContent().stream().map(c -> c.getPegawai().getId()).toList();
        List<CutiKuota> additionalData = repository.findByPegawaiIdInAndTahun(pegawaiIdList, request.getTahun() - 1);
        return CutiKuotaPegawaiResponse.builder()
                .page(page)
                .additional(CutiKuotaResponse.fromList(additionalData))
                .build();
    }

    @Override
    public CutiKuotaResponse findById(Long id) {
        return repository.findById(id).map(CutiKuotaResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(CutiKuotaPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Cuti Kuota sudah ada");
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiKuota entity = CutiKuotaPostRequest.toEntity(request, pegawai);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Kuota berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, CutiKuotaPutRequest request) {
        try {
            CutiKuota entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unknown Cuti Kuota"));
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiKuota toEntity = CutiKuotaPutRequest.toEntity(entity, request, pegawai);
            repository.save(toEntity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Kuota berhasil diupdate");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> importData(CutiKuotaImportRequest request) {
        return processCutiKuotaService.processCutiKuota(request);
    }

    @Override
    public boolean delete(Long id) {
        Optional<CutiKuota> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
