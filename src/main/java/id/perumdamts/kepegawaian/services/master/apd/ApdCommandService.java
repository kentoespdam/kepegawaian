package id.perumdamts.kepegawaian.services.master.apd;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.apd.ApdMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.ApdRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApdCommandService {
    private final ApdRepository repository;
    private final ProfesiRepository profesiRepository;

    @Transactional
    public SavedStatus<Long> create(Long profesiId, ApdPostRequest request) {
        Profesi profesi = profesiRepository.getReferenceById(profesiId);
        Apd entity = ApdMapper.toEntity(request, profesi);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, Long profesiId, ApdPostRequest request) {
        Apd existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Apd not found"));
        Profesi profesi = profesiRepository.getReferenceById(profesiId);
        ApdMapper.updateEntity(existing, request, profesi);
        repository.save(existing);
        return SavedStatus.build(ESaveStatus.SUCCESS, existing.getId());
    }

    @Transactional
    public boolean delete(Long id) {
        Apd existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Apd not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
        return true;
    }
}
