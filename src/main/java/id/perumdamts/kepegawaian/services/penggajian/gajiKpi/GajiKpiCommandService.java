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
        // Native carcass-finder — findByNipamAndPeriode() respects @SQLRestriction
        // and would hide soft-deleted rows, so the revive branch below would be
        // dead code (see kepegawaian-33s, ADR-0005).
        Optional<GajiKpi> existing = repository.findAnyByNipamAndPeriode(request.getNipam(), request.getPeriode());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                GajiKpi revived = existing.get();
                revived.setIsDeleted(false);
                GajiKpiMapper.updateEntity(revived, request);
                repository.save(revived);
                return SavedStatus.build(ESaveStatus.SUCCESS, revived.getId());
            }
            throw new ConflictException("Gaji KPI sudah ada");
        }
        GajiKpi entity = GajiKpiMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, GajiKpiPutRequest request) {
        GajiKpi entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gaji KPI not found"));
        // Live collision (different id).
        Optional<GajiKpi> duplicate = repository.findByNipamAndPeriode(request.getNipam(), request.getPeriode())
                .filter(one -> !one.getId().equals(id));
        if (duplicate.isPresent())
            throw new ConflictException("Gaji KPI sudah ada");
        // Archived collision (revive adalah create-only, sama seperti Organisasi).
        Optional<GajiKpi> archived = repository.findAnyByNipamAndPeriode(request.getNipam(), request.getPeriode());
        if (archived.isPresent() && Boolean.TRUE.equals(archived.get().getIsDeleted()) && !archived.get().getId().equals(id))
            throw new ConflictException("Gaji KPI dengan kombinasi sama sudah diarsip; buat baru untuk revive");
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
