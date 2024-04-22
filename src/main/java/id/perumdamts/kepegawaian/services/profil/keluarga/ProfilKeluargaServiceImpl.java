package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPutRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaResponse;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.ProfilKeluargaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilKeluargaServiceImpl implements ProfilKeluargaService {
    private final ProfilKeluargaRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;

    @Override
    public List<ProfilKeluargaResponse> findAll() {
        return repository.findAll().stream().map(ProfilKeluargaResponse::from).toList();
    }

    @Override
    public Page<ProfilKeluargaResponse> findPage(ProfilKeluargaRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(ProfilKeluargaResponse::from);
    }

    @Override
    public ProfilKeluargaResponse findById(Long id) {
        return repository.findById(id).map(ProfilKeluargaResponse::from).orElse(null);
    }

    @Override
    public List<ProfilKeluargaResponse> findByBiodataId(String biodataId) {
        return repository.findByBiodata_Nik(biodataId).stream().map(ProfilKeluargaResponse::from).toList();
    }

    @Transactional
    @Override
    public SavedStatus<?> save(ProfilKeluargaPostRequest request) {

        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");
        Optional<JenjangPendidikan> jenjangPendidikan = jenjangPendidikanRepository.findById(request.getPendidikanId());
        if (jenjangPendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenjang Pendidikan");
        ProfilKeluarga entity = ProfilKeluargaPostRequest.toEntity(request, biodata.get(), jenjangPendidikan.get());
        boolean profilKeluargaExist = repository.exists(Example.of(entity));
        if (profilKeluargaExist)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Profil Keluarga sudah ada");
        ProfilKeluarga save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, ProfilKeluargaPutRequest request) {
        Optional<ProfilKeluarga> profilKeluarga = repository.findById(id);
        if (profilKeluarga.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Profil Keluarga");
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");
        Optional<JenjangPendidikan> jenjangPendidikan = jenjangPendidikanRepository.findById(request.getPendidikanId());
        if (jenjangPendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenjang Pendidikan");
        ProfilKeluarga entity = ProfilKeluargaPutRequest.toEntity(request, profilKeluarga.get(), biodata.get(), jenjangPendidikan.get());
        ProfilKeluarga save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean delete(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
