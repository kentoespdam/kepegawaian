package id.perumdamts.kepegawaian.services.master.sanksi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.sanksi.*;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.repositories.master.JenisSpRepository;
import id.perumdamts.kepegawaian.repositories.master.SanksiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanksiServiceImpl implements SanksiService {
    private final SanksiRepository repository;
    private final JenisSpRepository jenisSpRepository;

    @Override
    public Page<SanksiResponse> findPage(SanksiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable()).map(SanksiResponse::from);
    }

    @Override
    public List<SanksiMiniResponse> list() {
        return repository.findAll().stream().map(SanksiMiniResponse::from).toList();
    }

    @Override
    public SanksiResponse findById(Long id) {
        return repository.findById(id).map(SanksiResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(SanksiPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Sanksi sudah ada");
            JenisSp jenisSp = jenisSpRepository.findById(request.getJenisSpId()).orElseThrow(() -> new RuntimeException("Unknown Jenis SP"));
            Sanksi entity = SanksiPostRequest.toEntity(request, jenisSp);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Sanksi berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, SanksiPutRequest request) {
        try {
            Sanksi entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Sanksi"));
            JenisSp jenisSp = jenisSpRepository.findById(request.getJenisSpId()).orElseThrow(() -> new RuntimeException("Unknown Jenis SP"));
            repository.save(SanksiPutRequest.toEntity(entity, request, jenisSp));
            return SavedStatus.build(ESaveStatus.SUCCESS, "Sanksi berhasil diupdate");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> updateJenisSp(Long id, PatchSanksiJenisSpRequest request) {
        Optional<Sanksi> byId = repository.findById(id);
        if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Sanksi");
        JenisSp jenisSp = jenisSpRepository.findById(request.getJenisSpId()).orElse(null);
        repository.save(PatchSanksiJenisSpRequest.toEntity(byId.get(), jenisSp));
        return SavedStatus.build(ESaveStatus.SUCCESS, "Jenis SP berhasil diupdate");
    }

    @Override
    public boolean delete(Long id) {
        Optional<Sanksi> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        Sanksi entity = byId.get();
        entity.setIsDeleted(true);
        repository.save(entity);
        return true;
    }
}
