package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKitasRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.KartuIdentitasRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KartuIdentitasServiceImpl implements KartuIdentitasService {
    private final KartuIdentitasRepository repository;
    private final JenisKitasRepository jenisKitasRepository;
    private final LampiranProfilService lampiranProfilService;
    private final BiodataRepository biodataRepository;
    private final PegawaiRepository pegawaiRepository;

    @Value("${custom.protected.delete.kartuIdentitas.ktp}")
    private Long PROTECTED_KARTU_IDENTITAS_ID;

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
    public Page<KartuIdentitasResponse> findByNik(String nik, KartuIdentitasRequest request) {
        request.setNik(nik);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(KartuIdentitasResponse::from);
    }

    @Override
    public KartuIdentitas execSave(KartuIdentitas kartuIdentitas) {
        return repository.findOne(Example.of(kartuIdentitas))
                .orElseGet(() -> repository.save(kartuIdentitas));
    }

    @Transactional
    @Override
    public SavedStatus<?> save(KartuIdentitasPostRequest request) {
        try {
            Biodata biodata = biodataRepository.findById(request.getNik())
                    .orElseThrow(() -> new RuntimeException("Biodata not found"));
            JenisKitas jenisKitas = jenisKitasRepository.findById(request.getJenisKartuId())
                    .orElseThrow(() -> new RuntimeException("Jenis Kartu not found"));
            KartuIdentitas entity = KartuIdentitasPostRequest.toEntity(request, biodata, jenisKitas);
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Kartu Identitas sudah ada");

            KartuIdentitas save = this.execSave(entity);
            if (save.getJenisKartu().getNama().equals("BPJS"))
                pegawaiRepository.findByBiodata_Nik(request.getNik())
                        .ifPresent(pegawai -> {
                            pegawai.setIsAskes(true);
                            pegawaiRepository.save(pegawai);
                        });
            return SavedStatus.build(ESaveStatus.SUCCESS, "Kartu Identitas Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, KartuIdentitasPutRequest request) {
        try {
            Biodata biodata = biodataRepository.findById(request.getNik())
                    .orElseThrow(() -> new RuntimeException("Biodata not found"));
            JenisKitas jenisKitas = jenisKitasRepository.findById(request.getJenisKartuId())
                    .orElseThrow(() -> new RuntimeException("Jenis Kartu not found"));

            KartuIdentitas kartuIdentitas = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Kartu Identitas not found"));

            if (kartuIdentitas.getJenisKartu().getNama().equals("BPJS") && !jenisKitas.getNama().equals("BPJS")) {
                pegawaiRepository.findByBiodata_Nik(request.getNik())
                        .ifPresent(pegawai -> {
                            pegawai.setIsAskes(false);
                            pegawaiRepository.save(pegawai);
                        });
            }

            KartuIdentitas entity = KartuIdentitasPutRequest.toEntity(request, kartuIdentitas, biodata, jenisKitas);
            KartuIdentitas save = this.execSave(entity);

            if (save.getJenisKartu().getNama().equals("BPJS"))
                pegawaiRepository.findByBiodata_Nik(request.getNik()).ifPresent(pegawai -> {
                    pegawai.setIsAskes(true);
                    pegawaiRepository.save(pegawai);
                });

            return SavedStatus.build(ESaveStatus.SUCCESS, "Kartu Identitas Updated");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<KartuIdentitas> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        if (byId.get().getJenisKartu().getId().equals(PROTECTED_KARTU_IDENTITAS_ID))
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        lampiranProfilService.deleteByRefId(EJenisLampiranProfil.KARTU_IDENTITAS, id);
        return true;
    }

    //lampiran
    @Override
    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.KARTU_IDENTITAS, id);
    }

    @Override
    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    @Override
    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.KARTU_IDENTITAS, id);
    }

    @Transactional
    @Override
    public SavedStatus<?> addLampiran(KartuIdentitasLampiranPostRequest request) {
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
