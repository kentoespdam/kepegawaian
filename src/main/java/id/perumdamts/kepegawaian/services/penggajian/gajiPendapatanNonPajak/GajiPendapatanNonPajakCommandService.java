package id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiPendapatanNonPajakRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPendapatanNonPajakCommandService {
    private final GajiPendapatanNonPajakRepository repository;

    @Transactional
    public SavedStatus<Long> save(GajiPendapatanNonPajakPostRequest request) {
        Optional<GajiPendapatanNonPajak> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            throw new ConflictException("Pendapatan Non Pajak sudah ada");
        GajiPendapatanNonPajak entity = GajiPendapatanNonPajakMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, GajiPendapatanNonPajakPutRequest request) {
        GajiPendapatanNonPajak entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pendapatan Non Pajak not found"));
        GajiPendapatanNonPajakMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public Boolean delete(Long id) {
        Optional<GajiPendapatanNonPajak> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
