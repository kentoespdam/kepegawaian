package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPostRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPutRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasResponse;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.repositories.master.JenisKitasRepository;
import id.perumdamts.kepegawaian.repositories.profil.KartuIdentitasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KartuIdentitasServiceImpl implements KartuIdentitasService {
    private final KartuIdentitasRepository repository;
    private final JenisKitasRepository jenisKitasRepository;

    @Override
    public List<KartuIdentitasResponse> findAll() {
        return repository.findAll().stream().map(KartuIdentitasResponse::from).toList();
    }

    @Override
    public Page<KartuIdentitasResponse> findPage(KartuIdentitasRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(KartuIdentitasResponse::from);
    }

    @Override
    public KartuIdentitasResponse findById(Long id) {
        return repository.findById(id).map(KartuIdentitasResponse::from).orElse(null);
    }

    @Override
    public List<KartuIdentitasResponse> findByNik(String nik) {
        return repository.findByBiodata_Nik(nik).stream()
                .map(KartuIdentitasResponse::from).toList();
    }

    @Override
    public KartuIdentitas execSave(KartuIdentitas kartuIdentitas) {
        return repository.save(kartuIdentitas);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(KartuIdentitasPostRequest request) {
        Optional<JenisKitas> jenisKitas = jenisKitasRepository.findById(request.getJenisKartu());
        if (jenisKitas.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Kartu Identitas");

        KartuIdentitas entity = KartuIdentitasPostRequest.toEntity(request, jenisKitas.get());
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Kartu Identitas sudah ada");

        KartuIdentitas save = this.execSave(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, KartuIdentitasPutRequest request) {
        Optional<JenisKitas> jenisKitas = jenisKitasRepository.findById(request.getJenisKartu());
        if (jenisKitas.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Kartu Identitas");

        Optional<KartuIdentitas> kartuIdentitas = repository.findById(id);
        if (kartuIdentitas.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Kartu Identitas");

        KartuIdentitas entity = KartuIdentitasPutRequest.toEntity(request, kartuIdentitas.get(), jenisKitas.get());
        KartuIdentitas save = this.execSave(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
