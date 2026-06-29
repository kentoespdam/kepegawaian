package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatKontrakRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSkRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiWriteback;
import id.perumdamts.kepegawaian.services.pegawai.port.KontrakBootstrapPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiwayatKontrakCommandService implements KontrakBootstrapPort {
    private final RiwayatKontrakRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final GolonganRepository golonganRepository;
    private final RiwayatSkRepository skRepository;
    private final PegawaiWriteback pegawaiWriteback;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RiwayatKontrak createKontrakFromPegawai(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatSk sk = new RiwayatSk();
        sk.setPegawai(pegawai);
        sk.setNipam(pegawai.getNipam());
        sk.setNama(pegawai.getBiodata().getNama());
        sk.setNomorSk(request.getNomorSk());
        sk.setJenisSk(EJenisSk.SK_LAINNYA);
        sk.setTanggalSk(request.getTanggalSk());
        sk.setTmtBerlaku(request.getTmtBerlakuSk());
        sk.setGajiPokok(request.getGajiPokok());
        sk.setNotes(request.getNotes());
        skRepository.save(sk);

        RiwayatKontrak entity = RiwayatKontrakPostRequest.toEntity(request, pegawai);
        entity.setIsLatest(true);
        RiwayatKontrak saved = repository.save(entity);
        updateLatest(saved);
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public RiwayatKontrak save(RiwayatKontrakPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) {
            throw new ConflictException("Riwayat Kontrak is Exists");
        }
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));

        RiwayatKontrak entity = RiwayatKontrakPostRequest.toEntity(request, pegawai);

        switch (request.getJenisKontrak()) {
            case PERPANJANGAN: {
                RiwayatSk sk = new RiwayatSk();
                sk.setPegawai(pegawai);
                sk.setNipam(pegawai.getNipam());
                sk.setNama(pegawai.getBiodata().getNama());
                sk.setNomorSk(request.getNomorKontrak());
                sk.setJenisSk(EJenisSk.SK_LAINNYA);
                sk.setTanggalSk(request.getTanggalSk());
                sk.setTmtBerlaku(request.getTanggalMulai());
                sk.setGajiPokok(request.getGajiPokok());
                sk.setNotes(request.getNotes());
                RiwayatSk savedSk = skRepository.save(sk);
                pegawaiWriteback.writebackKontrak(pegawai, savedSk, request.getTanggalSelesai());
                break;
            }
            case PENGANGKATAN: {
                Golongan golongan = golonganRepository.findById(request.getGolonganId())
                        .orElseThrow(() -> new NotFoundException("Unknown Golongan"));
                pegawai.setGolongan(golongan);
                RiwayatSk sk = new RiwayatSk();
                sk.setPegawai(pegawai);
                sk.setNipam(request.getNipam());
                sk.setNama(pegawai.getBiodata().getNama());
                sk.setNomorSk(request.getNomorKontrak());
                sk.setJenisSk(EJenisSk.SK_CAPEG);
                sk.setTanggalSk(request.getTanggalSk());
                sk.setTmtBerlaku(request.getTanggalMulai());
                sk.setGolongan(golongan);
                sk.setMkgbBulan(0);
                sk.setMkgTahun(0);
                sk.setGajiPokok(request.getGajiPokok());
                sk.setNotes(request.getNotes());
                RiwayatSk savedSk = skRepository.save(sk);
                pegawaiWriteback.writebackGolonganPensiun(pegawai, savedSk, request.getTanggalSelesai());
                break;
            }
        }

        RiwayatKontrak saved = repository.save(entity);
        if (Boolean.TRUE.equals(saved.getIsLatest())) {
            updateLatest(saved);
        }
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public RiwayatKontrak update(Long id, RiwayatKontrakPutRequest request) {
        RiwayatKontrak riwayatKontrak = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Riwayat Kontrak"));
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));

        RiwayatKontrak entity = RiwayatKontrakPutRequest.toEntity(riwayatKontrak, request, pegawai);

        switch (request.getJenisKontrak()) {
            case PERPANJANGAN: {
                RiwayatSk sk = new RiwayatSk();
                sk.setPegawai(pegawai);
                sk.setNipam(pegawai.getNipam());
                sk.setNama(pegawai.getBiodata().getNama());
                sk.setNomorSk(request.getNomorKontrak());
                sk.setJenisSk(EJenisSk.SK_LAINNYA);
                sk.setTanggalSk(request.getTanggalSk());
                sk.setTmtBerlaku(request.getTanggalMulai());
                sk.setGajiPokok(request.getGajiPokok());
                sk.setNotes(request.getNotes());
                RiwayatSk savedSk = skRepository.save(sk);
                pegawaiWriteback.writebackKontrak(pegawai, savedSk, request.getTanggalSelesai());
                break;
            }
            case PENGANGKATAN: {
                RiwayatSk sk = new RiwayatSk();
                sk.setPegawai(pegawai);
                sk.setNipam(request.getNipam());
                sk.setNama(pegawai.getBiodata().getNama());
                sk.setNomorSk(request.getNomorKontrak());
                sk.setJenisSk(EJenisSk.SK_CAPEG);
                sk.setTanggalSk(request.getTanggalSk());
                sk.setTmtBerlaku(request.getTanggalMulai());
                if (pegawai.getGolongan() != null) {
                    sk.setGolongan(pegawai.getGolongan());
                }
                sk.setMkgbBulan(0);
                sk.setMkgTahun(0);
                sk.setGajiPokok(request.getGajiPokok());
                sk.setNotes(request.getNotes());
                RiwayatSk savedSk = skRepository.save(sk);
                pegawaiWriteback.writebackGolonganPensiun(pegawai, savedSk, request.getTanggalSelesai());
                break;
            }
        }

        RiwayatKontrak saved = repository.save(entity);
        if (Boolean.TRUE.equals(saved.getIsLatest())) {
            updateLatest(saved);
        }
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        RiwayatKontrak byId = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat Kontrak not found"));
        byId.setIsDeleted(true);
        repository.save(byId);

        Specification<RiwayatSk> specification = (root, query, cb) -> cb.and(
                cb.equal(root.get("pegawai").get("id"), byId.getPegawai().getId()),
                cb.equal(root.get("nomorSk"), byId.getNomorKontrak())
        );
        skRepository.findAll(specification).forEach(sk -> {
            sk.setIsDeleted(true);
            skRepository.save(sk);
        });
    }

    private void updateLatest(RiwayatKontrak entity) {
        Specification<RiwayatKontrak> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pegawai").get("id"), entity.getPegawai().getId()),
                criteriaBuilder.notEqual(root.get("id"), entity.getId())
        );
        repository.findAll(specification).stream().peek(k -> k.setIsLatest(false)).forEach(repository::save);
    }
}
