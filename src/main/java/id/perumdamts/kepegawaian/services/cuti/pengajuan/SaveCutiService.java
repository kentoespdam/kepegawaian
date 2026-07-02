package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.SisaCutiRecord;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.helpers.cuti.CutiKuotaAllocator;
import id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.services.cuti.handlers.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SaveCutiService {
    private final CutiPegawaiRepository repository;
    private final CutiKuotaRepository cutiKuotaRepository;
    private final JabatanRepository jabatanRepository;
    private final CutiProperties cutiProperties;

    private final CutiNextYearHandler cutiNextYearHandler;
    private final CutiOverlappingYearHandler cutiOverlappingYearHandler;
    private final CutiBetween1JanAnd30JunHandler cutiBetween1JanAnd30JunHandler;
    private final CutiBetween1JulAnd31DecHandler cutiBetween1JulAnd31DecHandler;
    private final CutiBetween30JunAnd1JulHandler cutiBetween30JunAnd1JulHandler;

    public CutiPegawai forNextYear(CutiPengajuanPostRequest request, CutiPegawai entity) {
        cutiNextYearHandler.handle(request, entity);
        setPic(entity);
        return repository.save(entity);
    }

    public CutiPegawai overlappingYear(CutiPengajuanPostRequest request, CutiPegawai entity) {
        cutiOverlappingYearHandler.handle(request, entity);
        setPic(entity);
        return repository.save(entity);
    }

    public CutiPegawai between1JanAnd30Jun(CutiPengajuanPostRequest request, CutiPegawai entity) {
        cutiBetween1JanAnd30JunHandler.handle(request, entity);
        setPic(entity);
        return repository.save(entity);
    }

    public CutiPegawai between1JulAnd31Dec(CutiPengajuanPostRequest request, CutiPegawai entity) {
        cutiBetween1JulAnd31DecHandler.handle(request, entity);
        setPic(entity);
        return repository.save(entity);
    }

    public CutiPegawai between30JunAnd1Jul(CutiPengajuanPostRequest request, CutiPegawai entity) {
        cutiBetween30JunAnd1JulHandler.handle(request, entity);
        setPic(entity);
        return repository.save(entity);
    }

    public void saveCutiNonTahunan(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalHariCuti = entity.getJumlahHariKerja();
        int year = request.getTanggalMulai().getYear();

        int prevKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(
                        request.getPegawaiId(),
                        year - 1,
                        LocalDate.of(year, 6, 30),
                        SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElse(0);
        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        int totalRemainingQuota = currentKuota + prevKuota;
        MinimalCutiRule.check(totalHariCuti, totalRemainingQuota);

        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiIbadah())) {
            CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(currentKuota, 0, currentKuota);
            entity.setRiwayatKuota0(prevKuota);
            entity.setRiwayatPakai0(0);
            entity.setRiwayatSisa0(prevKuota);
            entity.setRiwayatKuota1(currentKuota);
            entity.setRiwayatPakai1(res.getRiwayatPakai1());
            entity.setRiwayatSisa1(res.getRiwayatSisa1());
            entity.setKuotaAwal(prevKuota + currentKuota);
            entity.setKuotaAkhir(prevKuota);
        } else {
            entity.setKuotaAwal(totalRemainingQuota);
            entity.setRiwayatKuota0(prevKuota);
            entity.setRiwayatKuota1(currentKuota);
        }

        repository.save(entity);
    }

    private void setPic(CutiPegawai cutiPegawai) {
        Jabatan jabatan = cutiPegawai.getPegawai().getJabatan();

        if (jabatan.getLevel().getId().equals(cutiProperties.getLevelManager())) {
            jabatanRepository.findById(cutiProperties.getSupervisorSdm()).ifPresent(cutiPegawai::setPicSaatIni);
        } else {
            cutiPegawai.setPicSaatIni(jabatan.getParent());
        }
    }
}
