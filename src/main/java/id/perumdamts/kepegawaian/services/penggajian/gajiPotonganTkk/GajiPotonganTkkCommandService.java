package id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPutRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiPotonganTkk.GajiPotonganTkkMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.LevelRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiPotonganTkkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPotonganTkkCommandService {
    private final GajiPotonganTkkRepository repository;
    private final LevelRepository levelRepository;
    private final GolonganRepository golonganRepository;

    @Transactional
    public SavedStatus<?> create(GajiPotonganTkkPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Potongan Tkk sudah ada");
        Level level = levelRepository.findById(request.getLevelId()).orElse(null);
        Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElse(null);
        GajiPotonganTkk entity = GajiPotonganTkkMapper.toEntity(request, level, golongan);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Gaji Potongan Tkk Saved");
    }

    @Transactional
    public SavedStatus<?> update(Long id, GajiPotonganTkkPutRequest request) {
        Optional<GajiPotonganTkk> byId = repository.findById(id);
        if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Gaji Potongan Tkk not found");
        Level level = levelRepository.findById(request.getLevelId()).orElse(null);
        Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElse(null);
        GajiPotonganTkk entity = byId.get();
        GajiPotonganTkkMapper.updateEntity(entity, request, level, golongan);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Gaji Potongan Tkk Updated");
    }

    @Transactional
    public Boolean delete(Long id) {
        Optional<GajiPotonganTkk> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
