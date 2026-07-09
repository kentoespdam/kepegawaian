package id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPutRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.detailDasarGaji.DetailDasarGajiMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.DasarGajiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.DetailDasarGajiRepository;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailDasarGajiCommandService {
    private final DetailDasarGajiRepository repository;
    private final DasarGajiRepository dasarGajiRepository;
    private final GolonganRepository golonganRepository;

    public DetailDasarGaji findDetailDasarGajiByGolonganAndMasaKerja(Long golonganId, Integer masaKerja) {
        Optional<Golongan> golongan = golonganRepository.findById(golonganId);
        if (golongan.isEmpty())
            throw new NotFoundException("Golongan not found: " + golonganId);
        Integer golonganKode = Integer.parseInt(golongan.get().getGolongan().split("\\.")[1]);
        Specification<DetailDasarGaji> specification = SpecificationBuilder.<DetailDasarGaji>of()
                .addEqual(golonganKode, "golonganKode")
                .addEqual(masaKerja, "mkg")
                .build();
        return repository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Detail Dasar Gaji not found"));
    }

    @Transactional
    public SavedStatus<Long> save(DetailDasarGajiPostRequest request) {
        DasarGaji dasarGaji = dasarGajiRepository.findById(request.getDasarGajiId())
                .orElseThrow(() -> new NotFoundException("Dasar Gaji not found"));
        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElseThrow(() -> new NotFoundException("Golongan not found"));
        DetailDasarGaji entity = DetailDasarGajiMapper.toEntity(request, dasarGaji, golongan);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<String> saveBatch(List<DetailDasarGajiPostRequest> requests) {
        List<DetailDasarGaji> entities = new ArrayList<>();
        for (DetailDasarGajiPostRequest request : requests) {
            DasarGaji dasarGaji = dasarGajiRepository.findById(request.getDasarGajiId())
                    .orElseThrow(() -> new NotFoundException("Dasar Gaji not found: " + request.getDasarGajiId()));
            Golongan golongan = golonganRepository.findById(request.getGolonganId())
                    .orElseThrow(() -> new NotFoundException("Golongan not found: " + request.getGolonganId()));
            DetailDasarGaji entity = DetailDasarGajiMapper.toEntity(request, dasarGaji, golongan);
            entities.add(entity);
        }
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, entities.size() + " success");
    }

    @Transactional
    public SavedStatus<Long> update(Long id, DetailDasarGajiPutRequest request) {
        DasarGaji dasarGaji = dasarGajiRepository.findById(request.getDasarGajiId())
                .orElseThrow(() -> new NotFoundException("Dasar Gaji not found"));
        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElseThrow(() -> new NotFoundException("Golongan not found"));
        DetailDasarGaji detailDasarGaji = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detail Dasar Gaji not found"));
        DetailDasarGajiMapper.updateEntity(detailDasarGaji, request, dasarGaji, golongan);
        repository.save(detailDasarGaji);
        return SavedStatus.build(ESaveStatus.SUCCESS, detailDasarGaji.getId());
    }

    @Transactional
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
