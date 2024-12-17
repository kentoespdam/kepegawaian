package id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPendapatanNonPajakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPendapatanNonPajakServiceImpl implements GajiPendapatanNonPajakService {
    private final GajiPendapatanNonPajakRepository repository;

    @Override
    public Page<GajiPendapatanNonPajakResponse> findPage(GajiPendapatanNonPajakRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiPendapatanNonPajakResponse::from);
    }

    @Override
    public List<GajiPendapatanNonPajakResponse> findAll() {
        return repository.findAll().stream()
                .map(GajiPendapatanNonPajakResponse::from)
                .toList();
    }

    @Override
    public GajiPendapatanNonPajakResponse findById(Long id) {
        return repository.findById(id)
                .map(GajiPendapatanNonPajakResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> save(GajiPendapatanNonPajakPostRequest request) {
        Optional<GajiPendapatanNonPajak> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Pendapatan Non Pajak sudah ada");
        GajiPendapatanNonPajak entity = GajiPendapatanNonPajakPostRequest.toEntity(request);
        GajiPendapatanNonPajak save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> update(Long id, GajiPendapatanNonPajakPutRequest request) {
        Optional<GajiPendapatanNonPajak> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pendapatan Non Pajak");
        GajiPendapatanNonPajak entity = GajiPendapatanNonPajakPutRequest.toEntity(byId.get(), request);
        GajiPendapatanNonPajak save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean delete(Long id) {
        Optional<GajiPendapatanNonPajak> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
