package id.perumdamts.kepegawaian.services.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiTunjanganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiTunjanganServiceImpl implements GajiTunjanganService {
    private final GajiTunjanganRepository repository;
    private final LevelRepository levelRepository;
    private final GolonganRepository golonganRepository;

    @Override
    public Page<GajiTunjanganResponse> findPage(GajiTunjanganRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiTunjanganResponse::from);
    }

    @Override
    public GajiTunjanganResponse findById(EJenisTunjangan jenis, Long id) {
        return repository.findByIdAndJenisTunjangan(id, jenis)
                .map(GajiTunjanganResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(EJenisTunjangan jenis, GajiTunjanganPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Tunjangan sudah ada");

            Level level = levelRepository.findById(request.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Unknown Level"));
            Golongan golongan = golonganRepository.findById(request.getGolonganId())
                    .orElse(null);

            GajiTunjangan entity = GajiTunjanganPostRequest.toEntity(request, level, golongan);
            GajiTunjangan save = repository.save(entity);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(EJenisTunjangan jenis, Long id, GajiTunjanganPutRequest request) {
        try {
            Optional<GajiTunjangan> byId = repository.findByIdAndJenisTunjangan(id, jenis);
            if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Gaji Tunjangan");

            Level level = levelRepository.findById(request.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Unknown Level"));
            Golongan golongan = golonganRepository.findById(request.getGolonganId())
                    .orElse(null);

            GajiTunjangan entity = GajiTunjanganPutRequest.toEntity(byId.get(), request, level, golongan);
            GajiTunjangan save = repository.save(entity);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean deleteById(EJenisTunjangan jenis, Long id) {
        boolean exists = repository.existsByIdAndJenisTunjangan(id, jenis);
        if (!exists) return false;
        repository.deleteById(id);
        return true;
    }
}
