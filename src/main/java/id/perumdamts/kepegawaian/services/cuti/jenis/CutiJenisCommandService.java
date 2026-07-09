package id.perumdamts.kepegawaian.services.cuti.jenis;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPutRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.cuti.jenis.CutiJenisMapper;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiJenisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CutiJenisCommandService {
    private final CutiJenisRepository repository;

    @Transactional
    public SavedStatus<Long> save(CutiJenisPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) {
            throw new ConflictException("Cuti Jenis sudah ada");
        }

        Optional<CutiJenis> deletedOpt = repository.findDeletedByName(request.getNama());
        if (deletedOpt.isPresent()) {
            CutiJenis revived = deletedOpt.get();
            CutiJenis parent = null;
            if (request.getParentId() != null) {
                parent = repository.getReferenceById(request.getParentId());
            }
            revived.setParent(parent);
            revived.setMaxHari(request.getMaxHari());
            revived.setPotongKuotaTahunan(request.getPotongKuotaTahunan());
            revived.setIsDeleted(false);
            repository.save(revived);
            return SavedStatus.build(ESaveStatus.SUCCESS, revived.getId());
        }

        CutiJenis parent = null;
        if (request.getParentId() != null) {
            parent = repository.getReferenceById(request.getParentId());
        }
        CutiJenis entity = CutiJenisMapper.toEntity(request, parent);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, CutiJenisPutRequest request) {
        CutiJenis entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cuti Jenis not found"));

        CutiJenis parent = null;
        if (request.getParentId() != null) {
            parent = repository.getReferenceById(request.getParentId());
        }

        CutiJenisMapper.updateEntity(entity, request, parent);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<CutiJenis> byId = repository.findById(id);
        if (byId.isEmpty()) {
            return false;
        }
        repository.delete(byId.get());
        return true;
    }
}
