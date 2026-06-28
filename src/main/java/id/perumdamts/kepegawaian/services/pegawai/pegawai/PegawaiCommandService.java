package id.perumdamts.kepegawaian.services.pegawai.pegawai;

import id.perumdamts.kepegawaian.config.PegawaiProperties;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.*;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.ProfesiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.RumahDinasRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPendapatanNonPajakRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiProfilRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.GenericKontrakService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkService;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PegawaiCommandService {
    private final PegawaiRepository repository;
    private final BiodataRepository biodataRepository;
    private final JabatanRepository jabatanRepository;
    private final OrganisasiRepository organisasiRepository;
    private final ProfesiRepository profesiRepository;
    private final GolonganRepository golonganRepository;
    private final BiodataCommandService biodataCommandService;
    private final RiwayatSkService riwayatSkService;
    private final GenericKontrakService genericKontrakService;
    private final GajiPendapatanNonPajakRepository gajiPendapatanNonPajakRepository;
    private final GajiProfilRepository gajiProfilRepository;
    private final RumahDinasRepository rumahDinasRepository;
    private final AuthService authService;
    private final PegawaiProperties pegawaiProperties;

    @Transactional(rollbackFor = Exception.class)
    public SavedStatus<Long> save(PegawaiPostRequest request) {
        Optional<Pegawai> oneByNipam = repository.findOneByNipam(request.getNipam());
        if (oneByNipam.isPresent()) {
            throw new ConflictException("Pegawai is Exist");
        }

        Biodata biodata = biodataRepository.findById(request.getNik())
                .orElseGet(() -> biodataCommandService.saveFromPegawai(request));

        if (request.getStatusPegawai().equals(EStatusPegawai.NON_PEGAWAI)) {
            return SavedStatus.build(ESaveStatus.SUCCESS, null);
        }

        Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
        Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
        Profesi profesi = request.getProfesiId() == null ? null : profesiRepository.findById(request.getProfesiId())
                .orElseThrow(() -> new NotFoundException("Unknown Profesi"));

        Golongan golongan = request.getGolonganId() == null ||
                pegawaiProperties.getExcludedGolonganStatuses().contains(request.getStatusPegawai()) ||
                pegawaiProperties.getExcludedJabatanIds().contains(request.getJabatanId()) ? null :
                golonganRepository.findById(request.getGolonganId())
                        .orElseThrow(() -> new NotFoundException("Unknown Golongan"));

        GajiPendapatanNonPajak kodePajak = gajiPendapatanNonPajakRepository
                .findById(request.getKodePajakId()).orElseThrow(() -> new NotFoundException("Unknown Kode Pajak"));

        Pegawai entity = PegawaiMapper.toEntity(request, biodata, jabatan, organisasi, profesi, golongan, kodePajak);
        Pegawai pegawai = repository.save(entity);

        switch (request.getStatusPegawai()) {
            case KONTRAK:
                genericKontrakService.saveFromPegawai(request, pegawai);
                break;
            case HONORER:
            case PEGAWAI:
                savePegawai(request, pegawai);
                break;
            default:
                saveCapeg(request, pegawai);
                break;
        }

        authService.createUser(pegawai);

        return SavedStatus.build(ESaveStatus.SUCCESS, pegawai.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public SavedStatus<?> saveBatch(List<PegawaiPostRequest> requests) {
        for (PegawaiPostRequest request : requests) {
            save(request);
        }
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public SavedStatus<Long> update(Long id, PegawaiPutRequest request) {
        Pegawai pegawai = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));

        Biodata biodata = biodataRepository.findById(request.getNik())
                .orElseThrow(() -> new NotFoundException("Unknown Biodata"));
        Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
        Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
        Profesi profesi = request.getProfesiId() == null ? null :
                profesiRepository.findById(request.getProfesiId()).orElseThrow(() -> new NotFoundException("Unknown Profesi"));

        Golongan golongan = request.getGolonganId() == null ||
                pegawaiProperties.getExcludedGolonganStatuses().contains(request.getStatusPegawai()) ||
                pegawaiProperties.getExcludedJabatanIds().contains(request.getJabatanId()) ? null :
                golonganRepository.findById(request.getGolonganId())
                        .orElseThrow(() -> new NotFoundException("Unknown Golongan"));

        GajiPendapatanNonPajak kodePajak = gajiPendapatanNonPajakRepository.findById(request.getKodePajakId())
                .orElseThrow(() -> new NotFoundException("Unknown Kode Pajak"));

        Pegawai entity = PegawaiMapper.toEntity(pegawai, request, biodata, jabatan, organisasi, profesi, golongan, kodePajak);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public SavedStatus<Long> patchGaji(Long id, PegawaiPatchGaji request) {
        Pegawai pegawai = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));

        GajiPendapatanNonPajak kodePajak = gajiPendapatanNonPajakRepository.findById(request.getKodePajakId())
                .orElseThrow(() -> new NotFoundException("Unknown Kode Pajak"));
        GajiProfil profilGaji = gajiProfilRepository.findById(request.getGajiProfilId())
                .orElseThrow(() -> new NotFoundException("Unknown Profil Gaji"));
        RumahDinas rumahDinas = rumahDinasRepository.findById(request.getRumahDinasId()).orElse(null);

        Pegawai entity = PegawaiMapper.toEntity(pegawai, request, kodePajak, profilGaji, rumahDinas);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public SavedStatus<Long> patchProfil(Long id, PegawaiPatchProfil request) {
        Pegawai pegawai = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));

        Golongan golongan = request.getGolonganId() == null ? null :
                golonganRepository.findById(request.getGolonganId()).orElse(null);
        Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
        Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
        Profesi profesi = profesiRepository.findById(request.getProfesiId()).orElse(null);

        Pegawai entity = PegawaiMapper.toEntity(pegawai, request, golongan, organisasi, jabatan, profesi);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        Optional<Pegawai> optionalPegawai = repository.findById(id);
        if (optionalPegawai.isEmpty()) {
            return false;
        }
        repository.delete(optionalPegawai.get());
        return true;
    }

    private void savePegawai(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatSk riwayatSk = riwayatSkService.savePegawai(request, pegawai);
        pegawai.setRefSkPegawaiId(riwayatSk.getId());
        pegawai.setMkgTahun(0);
        pegawai.setMkgBulan(0);
        repository.save(pegawai);
    }

    private void saveCapeg(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatSk riwayatSk = riwayatSkService.saveCapeg(request, pegawai);
        pegawai.setRefSkCapegId(riwayatSk.getId());
        pegawai.setMkgTahun(0);
        pegawai.setMkgBulan(0);
        repository.save(pegawai);
    }
}
