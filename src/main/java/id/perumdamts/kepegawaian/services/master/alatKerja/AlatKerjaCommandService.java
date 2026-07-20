package id.perumdamts.kepegawaian.services.master.alatKerja;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.alatKerja.AlatKerjaMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlatKerjaRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlatKerjaCommandService {
    private final AlatKerjaRepository repository;
    private final ProfesiRepository profesiRepository;

    @Transactional
    public SavedStatus<Long> create(Long profesiId, AlatKerjaPostRequest request) {
        Profesi profesi = profesiRepository.getReferenceById(profesiId);
        AlatKerja entity = AlatKerjaMapper.toEntity(request, profesi);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, Long profesiId, AlatKerjaPostRequest request) {
        AlatKerja existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AlatKerja not found"));
        Profesi profesi = profesiRepository.getReferenceById(profesiId);
        AlatKerjaMapper.updateEntity(existing, request, profesi);
        repository.save(existing);
        return SavedStatus.build(ESaveStatus.SUCCESS, existing.getId());
    }

    @Transactional
    public boolean delete(Long id) {
        AlatKerja existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AlatKerja not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
        return true;
    }
}
