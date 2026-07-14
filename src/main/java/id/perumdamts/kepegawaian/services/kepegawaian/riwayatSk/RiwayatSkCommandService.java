package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.kepegawaian.riwayatSk.RiwayatSkMapper;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSkRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkCommandService;
import id.perumdamts.kepegawaian.services.pegawai.port.SkBootstrapPort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RiwayatSkCommandService implements SkBootstrapPort {
    private final RiwayatSkRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final GolonganRepository golonganRepository;
    private final LampiranSkCommandService lampiranSkCommandService;

    @Transactional(rollbackFor = Exception.class)
    public RiwayatSk save(RiwayatSkPostRequest request) {
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElse(null);

        boolean exists = repository.exists(request.getSpecification());
        if (exists) {
            throw new ConflictException("Riwayat SK is Exists");
        }

        RiwayatSk entity = RiwayatSkMapper.toEntity(request, pegawai, golongan);
        RiwayatSk save = repository.save(entity);
        if (request.getUpdateMaster()) {
            this.updatePegawai(request, pegawai, save, golongan);
        }

        return save;
    }

    @Transactional(rollbackFor = Exception.class)
    public RiwayatSk update(Long id, RiwayatSkPutRequest request) {
        RiwayatSk riwayatSk = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Riwayat SK"));
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElse(null);

        RiwayatSk entity = RiwayatSkMapper.updateEntity(riwayatSk, request, pegawai, golongan);
        RiwayatSk save = repository.save(entity);
        if (request.getUpdateMaster()) {
            this.updatePegawai(request, pegawai, save, golongan);
        }

        return save;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        RiwayatSk byId = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat SK not found"));
        byId.setIsDeleted(true);
        repository.save(byId);
        lampiranSkCommandService.deleteByRefId(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RiwayatSk createSkCapeg(PegawaiPostRequest request, Pegawai pegawai) {
        Golongan golongan = golonganRepository.getReferenceById(request.getGolonganId());
        LocalDate kenaikanBerikutnya = LocalDate.now().plusYears(2);

        RiwayatSk entity = new RiwayatSk();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setJenisSk(EJenisSk.SK_CAPEG);
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTmtBerlakuSk());
        entity.setGolongan(golongan);
        entity.setMkgTahun(0);
        entity.setMkgBulan(0);
        entity.setKenaikanBerikutnya(kenaikanBerikutnya);
        entity.setMkgbTahun(2);
        entity.setMkgbBulan(0);

        return repository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RiwayatSk createSkPegawaiTetap(PegawaiPostRequest request, Pegawai pegawai) {
        Long[] excludeGolonganJabatan = {1L, 2L, 3L, 25L};
        Golongan golongan = ArrayUtils.contains(excludeGolonganJabatan, request.getJabatanId()) ||
                pegawai.getStatusPegawai() != EStatusPegawai.PEGAWAI ? null : golonganRepository.getReferenceById(request.getGolonganId());
        LocalDate kenaikanBerikutnya = LocalDate.now().plusYears(2);

        RiwayatSk entity = new RiwayatSk();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setJenisSk(EJenisSk.SK_PEGAWAI_TETAP);
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTmtBerlakuSk());
        if (Objects.nonNull(golongan)) {
            entity.setGolongan(golongan);
        }
        entity.setMkgTahun(0);
        entity.setMkgBulan(0);
        entity.setKenaikanBerikutnya(kenaikanBerikutnya);
        entity.setMkgbTahun(2);
        entity.setMkgbBulan(0);

        return repository.save(entity);
    }

    private void updatePegawai(RiwayatSkPostRequest request, Pegawai pegawai, RiwayatSk sk, Golongan golongan) {
        if (request.getGajiPokok() <= 0 || request.getGolonganId() <= 0) {
            return;
        }
        pegawai.setGajiPokok(request.getGajiPokok());
        pegawai.setGolongan(golongan);
        switch (request.getJenisSk()) {
            case SK_KENAIKAN_PANGKAT_GOLONGAN:
                pegawai.setRefSkGolId(sk.getId());
                pegawai.setTmtGolongan(request.getTmtBerlaku());
                pegawai.setMkgTahun(sk.getMkgTahun());
                pegawai.setMkgBulan(sk.getMkgBulan());
                break;
            case SK_CAPEG:
                pegawai.setStatusPegawai(EStatusPegawai.CAPEG);
                pegawai.setRefSkCapegId(sk.getId());
                break;
            case SK_PEGAWAI_TETAP:
                if (pegawai.getStatusPegawai().equals(EStatusPegawai.CAPEG)) {
                    pegawai.setStatusPegawai(EStatusPegawai.PEGAWAI);
                } else if (pegawai.getStatusPegawai().equals(EStatusPegawai.CALON_HONORER)) {
                    pegawai.setStatusPegawai(EStatusPegawai.HONORER);
                }
                pegawai.setRefSkPegawaiId(sk.getId());
                break;
            case SK_JABATAN:
                pegawai.setRefSkJabatanId(sk.getId());
                break;
            case SK_MUTASI:
                pegawai.setRefSkMutasiId(sk.getId());
                break;
            case SK_PENYESUAIAN_GAJI:
                break;
        }

        pegawaiRepository.save(pegawai);
    }
}
