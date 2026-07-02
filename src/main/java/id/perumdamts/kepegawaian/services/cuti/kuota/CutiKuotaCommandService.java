package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaImportRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPutRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.mapper.cuti.kuota.CutiKuotaMapper;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CutiKuotaCommandService {
    private final CutiKuotaRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final ProcessCutiKuotaService processCutiKuotaService;
    private final CutiKuotaTemplateBuilder cutiKuotaTemplateBuilder;

    @Transactional
    public SavedStatus<?> save(CutiKuotaPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) {
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Cuti Kuota sudah ada");
            }
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiKuota entity = CutiKuotaMapper.toEntity(request, pegawai);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Kuota berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public SavedStatus<?> update(Long id, CutiKuotaPutRequest request) {
        try {
            CutiKuota entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unknown Cuti Kuota"));
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiKuotaMapper.updateEntity(entity, request, pegawai);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Kuota berhasil diupdate");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<CutiKuota> byId = repository.findById(id);
        if (byId.isEmpty()) {
            return false;
        }
        repository.delete(byId.get());
        return true;
    }

    public SavedStatus<?> importData(CutiKuotaImportRequest request) {
        return processCutiKuotaService.processCutiKuota(request);
    }

    public ResponseEntity<?> exportTemplate() {
        return cutiKuotaTemplateBuilder.build();
    }
}
