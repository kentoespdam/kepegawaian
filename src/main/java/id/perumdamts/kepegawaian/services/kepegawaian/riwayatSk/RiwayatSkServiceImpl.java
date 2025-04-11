package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatSkRepository;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkService;
import id.perumdamts.kepegawaian.services.master.golongan.GolonganService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiwayatSkServiceImpl implements RiwayatSkService {
    private final RiwayatSkRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final GolonganService golonganService;
    private final GolonganRepository golonganRepository;
    private final LampiranSkService lampiranSkService;

    @Override
    public List<RiwayatSkResponse> findAll(RiwayatSkRequest request) {
        return repository.findAll(request.getSpecification()).stream().map(RiwayatSkResponse::from).toList();
    }

    @Override
    public Page<RiwayatSkResponse> findPage(RiwayatSkRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatSkResponse::from);
    }

    @Override
    public RiwayatSkResponse findById(Long id) {
        return repository.findById(id).map(RiwayatSkResponse::from).orElse(null);
    }

    @Override
    public List<RiwayatSkResponse> findByIds(List<Long> riwayatIds) {
        return repository.findByIdIn(riwayatIds).stream().map(RiwayatSkResponse::from).toList();
    }

    @Override
    public List<RiwayatSkResponse> findByPegawai(Long pegawaiId) {
        return repository.findByPegawai_Id(pegawaiId).stream().map(RiwayatSkResponse::from).toList();
    }

    @Override
    public Page<RiwayatSkResponse> findByPegawaiId(Long pegawaiId, RiwayatSkRequest request) {
        request.setPegawaiId(pegawaiId);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatSkResponse::from);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(RiwayatSkPostRequest request) {
        try {
            if (request.getTmtBerlaku().isBefore(request.getTanggalSk()))
                throw new RuntimeException("TMT Berlaku must be greater than tanggal. SK");
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            Golongan golongan = golonganRepository.findById(request.getGolonganId())
                    .orElse(null);

            Optional<RiwayatSk> one = repository.findOne(request.getSpecification());
            if (one.isPresent())
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Riwayat SK is Exists");

            RiwayatSk entity = RiwayatSkPostRequest.toEntity(request, pegawai, golongan);
            RiwayatSk save = repository.save(entity);
            if (request.getUpdateMaster())
                this.updatePegawai(request, pegawai, save, golongan);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public RiwayatSk saveCapeg(PegawaiPostRequest request, Pegawai pegawai) {
        Golongan golongan = golonganService.findGolonganById(request.getGolonganId());
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
    public RiwayatSk savePegawai(PegawaiPostRequest request, Pegawai pegawai) {
        Long[] excludeGolonganJabatan = {1L, 2L, 3L, 25L};
        Golongan golongan = ArrayUtils.contains(excludeGolonganJabatan, request.getJabatanId()) ||
                pegawai.getStatusPegawai() != EStatusPegawai.PEGAWAI ? null : golonganService.findGolonganById(request.getGolonganId());
        LocalDate kenaikanBerikutnya = LocalDate.now().plusYears(2);

        RiwayatSk entity = new RiwayatSk();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setJenisSk(EJenisSk.SK_PEGAWAI_TETAP);
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTmtBerlakuSk());
        if (Objects.nonNull(golongan))
            entity.setGolongan(golongan);
        entity.setMkgTahun(0);
        entity.setMkgBulan(0);
        entity.setKenaikanBerikutnya(kenaikanBerikutnya);
        entity.setMkgbTahun(2);
        entity.setMkgbBulan(0);

        return repository.save(entity);
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, RiwayatSkPutRequest request) {
        try {
            RiwayatSk riwayatSk = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Riwayat SK"));
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElse(null);

            RiwayatSk entity = RiwayatSkPutRequest.toEntity(riwayatSk, request, pegawai, golongan);
            RiwayatSk save = repository.save(entity);
            this.updatePegawai(request, pegawai, save, golongan);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public Boolean delete(Long id) {
        Optional<RiwayatSk> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        lampiranSkService.deleteByRefId(id);
        return true;
    }

    private void updatePegawai(RiwayatSkPostRequest request, Pegawai pegawai, RiwayatSk sk, Golongan golongan) {
        if (request.getGajiPokok() <= 0 || request.getGolonganId() <= 0) return;
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
                if (pegawai.getStatusPegawai().equals(EStatusPegawai.CAPEG))
                    pegawai.setStatusPegawai(EStatusPegawai.PEGAWAI);
                else if (pegawai.getStatusPegawai().equals(EStatusPegawai.CALON_HONORER))
                    pegawai.setStatusPegawai(EStatusPegawai.HONORER);
                pegawai.setRefSkPegawaiId(sk.getId());
                break;
            case SK_JABATAN:
                pegawai.setRefSkJabatanId(sk.getId());
                break;
            case SK_MUTASI:
                pegawai.setRefSkMutasiId(sk.getId());
                break;
            case SK_PENYESUAIAN_GAJI:
        }

        pegawaiRepository.save(pegawai);
    }
}
