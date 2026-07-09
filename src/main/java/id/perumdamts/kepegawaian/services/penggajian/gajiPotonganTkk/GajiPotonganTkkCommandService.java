package id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPutRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
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
    public SavedStatus<Long> create(GajiPotonganTkkPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) throw new ConflictException("Gaji Potongan Tkk sudah ada");
        Level level = levelRepository.findById(request.getLevelId()).orElse(null);
        Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElse(null);
        GajiPotonganTkk entity = GajiPotonganTkkMapper.toEntity(request, level, golongan);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, GajiPotonganTkkPutRequest request) {
        GajiPotonganTkk entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gaji Potongan Tkk not found"));
        Level level = levelRepository.findById(request.getLevelId()).orElse(null);
        Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElse(null);
        GajiPotonganTkkMapper.updateEntity(entity, request, level, golongan);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
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
