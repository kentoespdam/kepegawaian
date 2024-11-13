package id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkResponse;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPotonganTkkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPotonganTkkServiceImpl implements GajiPotonganTkkService {
    private final GajiPotonganTkkRepository repository;
    private final LevelRepository levelRepository;
    private final GolonganRepository golonganRepository;

    @Override
    public Page<GajiPotonganTkkResponse> findPage(GajiPotonganTkkRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiPotonganTkkResponse::from);
    }

    @Override
    public GajiPotonganTkkResponse findById(Long id) {
        return repository.findById(id).map(GajiPotonganTkkResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> create(GajiPotonganTkkPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Potongan Tkk sudah ada");
        Level level = levelRepository.findById(request.getLevelId()).orElse(null);
        Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElse(null);
        GajiPotonganTkk entity = GajiPotonganTkkPostRequest.toEntity(request, level, golongan);
        GajiPotonganTkk save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> update(Long id, GajiPotonganTkkPutRequest request) {
        Optional<GajiPotonganTkk> byId = repository.findById(id);
        if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Gaji Potongan Tkk not found");
        Level level = levelRepository.findById(request.getLevelId()).orElse(null);
        Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElse(null);
        GajiPotonganTkk entity = GajiPotonganTkkPutRequest.toEntity(byId.get(), request, level, golongan);
        GajiPotonganTkk save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean delete(Long id) {
        Optional<GajiPotonganTkk> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
