package id.perumdamts.kepegawaian.services.cuti.jenis;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPutRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
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
    public SavedStatus<?> save(CutiJenisPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) {
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Cuti Jenis sudah ada");
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
                return SavedStatus.build(ESaveStatus.SUCCESS, "Data Saved!");
            }

            CutiJenis parent = null;
            if (request.getParentId() != null) {
                parent = repository.getReferenceById(request.getParentId());
            }
            CutiJenis entity = CutiJenisMapper.toEntity(request, parent);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Data Saved!");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public SavedStatus<?> update(Long id, CutiJenisPutRequest request) {
        try {
            Optional<CutiJenis> byId = repository.findById(id);
            if (byId.isEmpty()) {
                return SavedStatus.build(ESaveStatus.FAILED, "Data Not Found!");
            }

            CutiJenis parent = null;
            if (request.getParentId() != null) {
                parent = repository.getReferenceById(request.getParentId());
            }

            CutiJenis entity = byId.get();
            CutiJenisMapper.updateEntity(entity, request, parent);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Data Updated!");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
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
