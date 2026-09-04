package id.perumdamts.kepegawaian.services.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiKpi.GajiKpiMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKpiRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiKpiCommandService {
    private final GajiKpiRepository repository;

    @Transactional
    public SavedStatus<Long> save(GajiKpiPostRequest request) {
        Optional<GajiKpi> one = repository.findByNipamAndPeriode(request.getNipam(), request.getPeriode());
        if (one.isPresent())
            throw new ConflictException("Gaji KPI sudah ada");
        GajiKpi entity = GajiKpiMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, GajiKpiPutRequest request) {
        GajiKpi entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gaji KPI not found"));
        Optional<GajiKpi> duplicate = repository.findByNipamAndPeriode(request.getNipam(), request.getPeriode())
                .filter(one -> !one.getId().equals(id));
        if (duplicate.isPresent())
            throw new ConflictException("Gaji KPI sudah ada");
        GajiKpiMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public Boolean delete(Long id) {
        Optional<GajiKpi> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
