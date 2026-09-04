package id.perumdamts.kepegawaian.services.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiTunjangan.GajiTunjanganMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.LevelRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiTunjanganRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiTunjanganCommandService {
    private final GajiTunjanganRepository repository;
    private final LevelRepository levelRepository;
    private final GolonganRepository golonganRepository;

    @Transactional
    @CacheEvict(value = "gaji-referensi", key = "'tunjangan'")
    public SavedStatus<Long> save(EJenisTunjangan jenis, GajiTunjanganPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) throw new ConflictException("Gaji Tunjangan sudah ada");

        Level level = levelRepository.findById(request.getLevelId())
                .orElseThrow(() -> new NotFoundException("Level not found"));
        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElse(null);

        GajiTunjangan entity = GajiTunjanganMapper.toEntity(request, level, golongan);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    @CacheEvict(value = "gaji-referensi", key = "'tunjangan'")
    public SavedStatus<Long> update(EJenisTunjangan jenis, Long id, GajiTunjanganPutRequest request) {
        GajiTunjangan entity = repository.findByIdAndJenisTunjangan(id, jenis)
                .orElseThrow(() -> new NotFoundException("Gaji Tunjangan not found"));

        Level level = levelRepository.findById(request.getLevelId())
                .orElseThrow(() -> new NotFoundException("Level not found"));
        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElse(null);

        GajiTunjanganMapper.updateEntity(entity, request, level, golongan);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    @CacheEvict(value = "gaji-referensi", key = "'tunjangan'")
    public boolean deleteById(EJenisTunjangan jenis, Long id) {
        Optional<GajiTunjangan> byId = repository.findByIdAndJenisTunjangan(id, jenis);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
