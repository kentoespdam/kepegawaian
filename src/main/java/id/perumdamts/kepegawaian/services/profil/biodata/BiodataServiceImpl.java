package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BiodataServiceImpl implements BiodataService {
    private final BiodataRepository repository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final KartuIdentitasService kartuIdentitasService;

    @Override
    public List<BiodataResponse> findAll() {
        return repository.findAll().stream().map(BiodataResponse::from).toList();
    }

    @Override
    public Page<BiodataResponse> findPage(BiodataRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(BiodataResponse::from);
    }

    @Override
    public BiodataResponse findById(String id) {
        return repository.findById(id).map(BiodataResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(BiodataPostRequest request) {
        Optional<JenjangPendidikan> pendidikanTerakhir = jenjangPendidikanRepository.findById(request.getPendidikanTerakhir());
        if (pendidikanTerakhir.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Pendidikan Terakhir Tidak Valid");

        Biodata entity = BiodataPostRequest.toEntity(request, pendidikanTerakhir.get());
        boolean isBiodataExist = repository.exists(request.getSpecification());
        if (isBiodataExist)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Biodata sudah ada");

        Biodata save = repository.save(entity);
        kartuIdentitasService.execSave(new KartuIdentitas(save));
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> update(String id, BiodataPutRequest request) {
        Optional<JenjangPendidikan> pendidikanTerakhir = jenjangPendidikanRepository.findById(request.getPendidikanTerakhir());
        if (pendidikanTerakhir.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Pendidikan Terakhir Tidak Valid");

        Biodata entity = BiodataPutRequest.toEntity(request, pendidikanTerakhir.get());
        Optional<Biodata> biodata = repository.findById(id);
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        Biodata save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(String id) {
        boolean isBiodataExist = repository.existsById(id);
        if (!isBiodataExist)
            return false;
        repository.deleteById(id);
        return true;
    }
}
