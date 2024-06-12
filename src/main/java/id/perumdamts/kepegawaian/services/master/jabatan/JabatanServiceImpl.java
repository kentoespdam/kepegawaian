package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPutRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanResponse;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JabatanServiceImpl implements JabatanService {
    private final JabatanRepository repository;
    private final OrganisasiRepository organisasiRepository;
    private final LevelRepository levelRepository;

    @Override
    public List<JabatanResponse> findAll() {
        return repository.findAll().stream().map(JabatanResponse::from).toList();
    }

    @Override
    public Page<JabatanResponse> findPage(JabatanRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable()).map(JabatanResponse::from);
    }

    @Override
    public JabatanResponse findById(Long id) {
        return repository.findById(id).map(JabatanResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JabatanPostRequest request) {
        try {
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Level level = levelRepository.findById(request.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Unknown Level"));
            Jabatan parent = Objects.nonNull(request.getParentId()) ?
                    repository.findById(request.getParentId()).orElse(null) :
                    null;
            Optional<Jabatan> jabatan = repository.findOne(request.getSpecification());
            if (jabatan.isPresent())
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Jabatan sudah ada");
            Jabatan entity = JabatanPostRequest.toEntity(
                    request,
                    parent,
                    organisasi,
                    level
            );
            Jabatan save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JabatanPostRequest> requests) {
        try {
            List<Jabatan> entities = requests.stream().map(request -> {
                Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                        .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
                Level level = levelRepository.findById(request.getLevelId())
                        .orElseThrow(() -> new RuntimeException("Unknown Level"));
                Jabatan parent = Objects.nonNull(request.getParentId()) ?
                        repository.findById(request.getParentId()).orElse(null) :
                        null;
                return JabatanPostRequest.toEntity(
                        request,
                        parent,
                        organisasi,
                        level
                );
            }).toList();
            repository.saveAll(entities);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, JabatanPutRequest request) {
        try {
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Level level = levelRepository.findById(request.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Unknown Level"));
            Jabatan parent = repository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Unknown Parent"));

            Optional<Jabatan> jabatan = repository.findById(id);
            if (jabatan.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jabatan");

            Jabatan entity = JabatanPutRequest.toEntity(
                    jabatan.get(),
                    request,
                    parent,
                    organisasi,
                    level
            );
            Jabatan save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Jabatan> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
