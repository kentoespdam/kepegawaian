package id.perumdamts.kepegawaian.services.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.*;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.PengalamanKerjaRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PengalamanKerjaServiceImpl implements PengalamanKerjaService {
    private final PengalamanKerjaRepository repository;
    private final BiodataRepository biodataRepository;
    private final LampiranProfilService lampiranProfilService;

    @Override
    public List<PengalamanKerjaResponse> findAll() {
        return repository.findAll().stream().map(PengalamanKerjaResponse::from).toList();
    }

    @Override
    public Page<PengalamanKerjaResponse> findPage(PengalamanKerjaRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(PengalamanKerjaResponse::from);
    }

    @Override
    public PengalamanKerjaResponse findById(Long id) {
        return repository.findById(id).map(PengalamanKerjaResponse::from)
                .orElse(null);
    }

    @Override
    public List<PengalamanKerjaResponse> findByBiodataId(String biodataId) {
        return repository.findByBiodata_Nik(biodataId).stream()
                .map(PengalamanKerjaResponse::from).toList();
    }

    @Transactional
    @Override
    public SavedStatus<?> save(PengalamanKerjaPostRequest request) {
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Pengalaman Kerja sudah ada");

        PengalamanKerja entity = PengalamanKerjaPostRequest.toEntity(request, biodata.get());
        PengalamanKerja save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, PengalamanKerjaResponse.from(save));
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, PengalamanKerjaPutRequest request) {
        Optional<PengalamanKerja> pengalamanKerja = repository.findById(id);
        if (pengalamanKerja.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pengalaman Kerja");

        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        PengalamanKerja entity = PengalamanKerjaPutRequest.toEntity(request, pengalamanKerja.get(), biodata.get());
        PengalamanKerja save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, PengalamanKerjaResponse.from(save));
    }

    @Transactional
    @Override
    public SavedStatus<?> acceptPengalamanKerja(Long id, PengalamanKerjaAcceptRequest request, String username) {
        Optional<PengalamanKerja> pengalamanKerja = repository.findById(id);
        if (pengalamanKerja.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pengalaman Kerja");

        PengalamanKerja entity = pengalamanKerja.get();
        entity.setDisetujui(true);
        entity.setDisetujuiOleh(username);
        entity.setTanggalDisetujui(LocalDateTime.now());
        PengalamanKerja save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, PengalamanKerjaResponse.from(save));
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

    //lampiran
    @Override
    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
    }

    @Override
    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    @Override
    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
    }

    @Transactional
    @Override
    public SavedStatus<?> addLampiran(PengalamanLampiranPostRequest request) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Kartu Identitas");

        return lampiranProfilService.addLampiran(request);
    }

    @Override
    public Boolean deleteLampiran(Long id) {
        return lampiranProfilService.deleteById(id);
    }
}
