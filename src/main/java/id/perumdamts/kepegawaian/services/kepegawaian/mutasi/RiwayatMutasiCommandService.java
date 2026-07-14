package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.kepegawaian.riwayatMutasi.RiwayatMutasiMapper;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatMutasiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.ProfesiRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkCommandService;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiWriteback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiwayatMutasiCommandService {
    private final RiwayatMutasiRepository repository;
    private final RiwayatSkCommandService skCommandService;
    private final GolonganRepository golonganRepository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final ProfesiRepository profesiRepository;
    private final PegawaiWriteback pegawaiWriteback;

    @Transactional(rollbackFor = Exception.class)
    public RiwayatMutasi save(RiwayatMutasiPostRequest request) {
        request.setJenisSk(resolveJenisSk(request.getJenisMutasi()));
        boolean exists = repository.exists(request.getSpecificationMutasi());
        if (exists) {
            throw new ConflictException("Riwayat Mutasi is already Exists");
        }

        RiwayatSk riwayatSk = skCommandService.save(request);

        RiwayatMutasi entity;
        if (request.getJenisMutasi() == EJenisMutasi.MUTASI_GOLONGAN ||
                request.getJenisMutasi() == EJenisMutasi.MUTASI_GAJI ||
                request.getJenisMutasi() == EJenisMutasi.MUTASI_GAJI_BERKALA) {

            Golongan golonganBaru = golonganRepository.findById(request.getGolonganId())
                    .orElseThrow(() -> new NotFoundException("Unknown Golongan"));
            Golongan golonganLama = golonganRepository.findById(request.getGolonganLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Golongan"));

            entity = RiwayatMutasiMapper.toEntity(request, riwayatSk, golonganBaru, golonganLama);
            pegawaiWriteback.writebackGolongan(riwayatSk.getPegawai(), riwayatSk);
        } else {
            Organisasi organisasiBaru = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
            Organisasi organisasiLama = organisasiRepository.findById(request.getOrganisasiLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
            Jabatan jabatanBaru = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
            Jabatan jabatanLama = jabatanRepository.findById(request.getJabatanLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
            Profesi profesiBaru = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new NotFoundException("Unknown Profesi"));
            Profesi profesiLama = profesiRepository.findById(request.getProfesiLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Profesi"));

            entity = RiwayatMutasiMapper.toEntity(request, riwayatSk, organisasiBaru, jabatanBaru, profesiBaru, organisasiLama, jabatanLama, profesiLama);
            pegawaiWriteback.writebackJabatan(riwayatSk.getPegawai(), riwayatSk, organisasiBaru, jabatanBaru, profesiBaru);
        }

        return repository.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public RiwayatMutasi update(Long id, RiwayatMutasiPutRequest request) {
        request.setJenisSk(resolveJenisSk(request.getJenisMutasi()));
        RiwayatMutasi riwayatMutasi = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Riwayat Mutasi"));

        RiwayatSkPutRequest skPutRequest = new RiwayatSkPutRequest();
        skPutRequest.setPegawaiId(request.getPegawaiId());
        skPutRequest.setNomorSk(request.getNomorSk());
        skPutRequest.setJenisSk(request.getJenisSk());
        skPutRequest.setTanggalSk(request.getTanggalSk());
        skPutRequest.setTmtBerlaku(request.getTmtBerlaku());
        skPutRequest.setGolonganId(request.getGolonganId());
        skPutRequest.setGajiPokok(request.getGajiPokok());
        skPutRequest.setMkgTahun(request.getMkgTahun());
        skPutRequest.setMkgBulan(request.getMkgBulan());
        skPutRequest.setKenaikanBerikutnya(request.getKenaikanBerikutnya());
        skPutRequest.setMkgbTahun(request.getMkgbTahun());
        skPutRequest.setMkgbBulan(request.getMkgbBulan());
        skPutRequest.setUpdateMaster(request.getUpdateMaster());
        skPutRequest.setNotes(request.getNotes());

        RiwayatSk riwayatSk = skCommandService.update(riwayatMutasi.getRiwayatSk().getId(), skPutRequest);

        RiwayatMutasi entity;
        if (request.getJenisMutasi() == EJenisMutasi.MUTASI_GOLONGAN ||
                request.getJenisMutasi() == EJenisMutasi.MUTASI_GAJI ||
                request.getJenisMutasi() == EJenisMutasi.MUTASI_GAJI_BERKALA) {

            Golongan golonganBaru = golonganRepository.findById(request.getGolonganId())
                    .orElseThrow(() -> new NotFoundException("Unknown Golongan"));
            Golongan golonganLama = golonganRepository.findById(request.getGolonganLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Golongan"));

            entity = RiwayatMutasiMapper.updateEntity(riwayatMutasi, riwayatSk, request, golonganBaru, golonganLama);
            pegawaiWriteback.writebackGolongan(riwayatSk.getPegawai(), riwayatSk);
        } else {
            Organisasi organisasiBaru = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
            Organisasi organisasiLama = organisasiRepository.findById(request.getOrganisasiLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
            Jabatan jabatanBaru = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
            Jabatan jabatanLama = jabatanRepository.findById(request.getJabatanLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));
            Profesi profesiBaru = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new NotFoundException("Unknown Profesi"));
            Profesi profesiLama = profesiRepository.findById(request.getProfesiLamaId())
                    .orElseThrow(() -> new NotFoundException("Unknown Profesi"));

            entity = RiwayatMutasiMapper.updateEntity(riwayatMutasi, riwayatSk, request, organisasiBaru, jabatanBaru, profesiBaru, organisasiLama, jabatanLama, profesiLama);
            pegawaiWriteback.writebackJabatan(riwayatSk.getPegawai(), riwayatSk, organisasiBaru, jabatanBaru, profesiBaru);
        }

        return repository.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        RiwayatMutasi byId = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat Mutasi not found"));
        skCommandService.delete(byId.getRiwayatSk().getId());
        byId.setIsDeleted(true);
        repository.save(byId);
        return true;
    }

    private EJenisSk resolveJenisSk(EJenisMutasi jenisMutasi) {
        return switch (jenisMutasi) {
            case MUTASI_LOKER -> EJenisSk.SK_MUTASI;
            case MUTASI_GOLONGAN -> EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN;
            case MUTASI_GAJI -> EJenisSk.SK_PENYESUAIAN_GAJI;
            case MUTASI_GAJI_BERKALA -> EJenisSk.SK_KENAIKAN_GAJI_BERKALA;
            case MUTASI_JABATAN -> EJenisSk.SK_JABATAN;
            default -> throw new IllegalStateException("Unexpected value: " + jenisMutasi);
        };
    }
}
