package id.perumdamts.kepegawaian.services.master.alatKerja;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPutRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaResponse;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.repositories.master.AlatKerjaRepository;
import id.perumdamts.kepegawaian.repositories.master.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlatKerjaServiceImpl implements AlatKerjaService {
    private final AlatKerjaRepository repository;
    private final ProfesiRepository profesiRepository;

    @Override
    public List<AlatKerjaResponse> findAll() {
        return repository.findAll().stream().map(AlatKerjaResponse::from).toList();
    }

    @Override
    public Page<AlatKerjaResponse> findPage(AlatKerjaRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(AlatKerjaResponse::from);
    }

    @Override
    public AlatKerjaResponse findById(Long id) {
        return repository.findById(id).map(AlatKerjaResponse::from).orElse(null);
    }

    @Override
    public List<AlatKerjaResponse> findByProfesiId(Long id) {
        return repository.findByProfesi_Id(id).stream().map(AlatKerjaResponse::from).toList();
    }

    @Transactional
    @Override
    public SavedStatus<?> save(AlatKerjaPostRequest request) {
        try {
            Profesi profesi = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Alat Kerja sudah ada");
            AlatKerja entity = AlatKerjaPostRequest.toEntity(request, profesi);
            AlatKerja save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<AlatKerjaPostRequest> requests) {
        return null;
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, AlatKerjaPutRequest request) {
        try {
            Profesi profesi = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            Optional<AlatKerja> AlatKerja = repository.findById(id);
            if (AlatKerja.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Alat Kerja");
            AlatKerja entity = AlatKerjaPutRequest.toEntity(AlatKerja.get(), request, profesi);
            AlatKerja save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<AlatKerja> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}