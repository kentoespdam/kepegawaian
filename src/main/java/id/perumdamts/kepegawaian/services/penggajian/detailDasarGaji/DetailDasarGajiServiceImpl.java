package id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.DasarGajiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.DetailDasarGajiRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailDasarGajiServiceImpl implements DetailDasarGajiService {
    private final DetailDasarGajiRepository repository;
    private final DasarGajiRepository dasarGajiRepository;
    private final GolonganRepository golonganRepository;

    @Override
    public List<DetailDasarGajiResponse> findAll(DetailDasarGajiRequest request) {
        return repository.findAll(request.getSpecification()).stream()
                .map(DetailDasarGajiResponse::from).toList();
    }

    @Override
    public Page<DetailDasarGajiResponse> findPage(DetailDasarGajiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(DetailDasarGajiResponse::from);
    }

    @Override
    public DetailDasarGajiResponse findById(Long id) {
        return repository.findById(id).map(DetailDasarGajiResponse::from).orElse(null);
    }

    @Override
    public DetailDasarGaji findDetailDasarGajiByGolonganAndMasaKerja(Long golonganId, Integer masaKerja) {
        Optional<Golongan> golongan = golonganRepository.findById(golonganId);
        if (golongan.isEmpty())
            throw new RuntimeException("Golongan not found: " + golonganId);
        Integer golonganKode = Integer.parseInt(golongan.get().getGolongan().split("\\.")[1]);
        Specification<DetailDasarGaji> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("golonganKode"), golonganKode);
        Specification<DetailDasarGaji> masaKerjaSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("mkg"), masaKerja);
        Specification<DetailDasarGaji> andSpec = Specification.where(specification).and(masaKerjaSpec);
        return repository.findOne(andSpec)
                .orElseThrow(() -> new RuntimeException("Detail Dasar Gaji not found"));
    }

    @Transactional
    @Override
    public SavedStatus<?> save(DetailDasarGajiPostRequest request) {
        try {
            Optional<DasarGaji> dasarGaji = dasarGajiRepository.findById(request.getDasarGajiId());
            if (dasarGaji.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Dasar Gaji not found");
            Optional<Golongan> golongan = golonganRepository.findById(request.getGolonganId());
            if (golongan.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Golongan not found");
            DetailDasarGaji entity = DetailDasarGajiPostRequest.toEntity(request, dasarGaji.get(), golongan.get());
            DetailDasarGaji save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<DetailDasarGajiPostRequest> requests) {
        try {
            List<DetailDasarGaji> entities = new ArrayList<>();
            for (DetailDasarGajiPostRequest request : requests) {
                Optional<DasarGaji> dasarGaji = dasarGajiRepository.findById(request.getDasarGajiId());
                if (dasarGaji.isEmpty())
                    throw new RuntimeException("Dasar Gaji not found: " + request.getDasarGajiId());
                Optional<Golongan> golongan = golonganRepository.findById(request.getGolonganId());
                if (golongan.isEmpty())
                    throw new RuntimeException("Golongan not found: " + request.getGolonganId());
                DetailDasarGaji entity = DetailDasarGajiPostRequest.toEntity(request, dasarGaji.get(), golongan.get());
                entities.add(entity);
            }
            repository.saveAll(entities);
            return SavedStatus.build(ESaveStatus.SUCCESS, entities);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, DetailDasarGajiPutRequest request) {
        try {
            Optional<DasarGaji> dasarGaji = dasarGajiRepository.findById(request.getDasarGajiId());
            if (dasarGaji.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Dasar Gaji not found");
            Optional<Golongan> golongan = golonganRepository.findById(request.getGolonganId());
            if (golongan.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Golongan not found");
            Optional<DetailDasarGaji> byId = repository.findById(id);
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Detail Dasar Gaji not found");
            DetailDasarGaji entity = DetailDasarGajiPutRequest.toEntity(byId.get(), request, dasarGaji.get(), golongan.get());
            DetailDasarGaji save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
