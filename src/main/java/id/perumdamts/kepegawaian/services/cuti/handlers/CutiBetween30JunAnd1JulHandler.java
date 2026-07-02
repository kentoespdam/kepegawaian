package id.perumdamts.kepegawaian.services.cuti.handlers;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.SisaCutiRecord;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.cuti.CutiKuotaAllocator;
import id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule;
import id.perumdamts.kepegawaian.helpers.cuti.WorkdayCalculator;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CutiBetween30JunAnd1JulHandler implements CutiPeriodHandler {
    private final CutiKuotaRepository cutiKuotaRepository;
    private final HariLiburRepository hariLiburRepository;

    @Override
    public void handle(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalHariCuti = entity.getJumlahHariKerja();
        int year = request.getTanggalMulai().getYear();

        int prevKuota = cutiKuotaRepository
                .findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(request.getPegawaiId(), year - 1, request.getTanggalSelesai(), SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);

        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        int totalRemainingQuota = currentKuota + prevKuota;

        MinimalCutiRule.check(totalHariCuti, totalRemainingQuota);

        CutiKuotaAllocationResult res;
        if (prevKuota == 0) {
            res = CutiKuotaAllocator.allocate(totalHariCuti, 0, currentKuota);
        } else {
            LocalDate targetJune30 = DateHelper.generateDate(year, 6, 30);
            Set<LocalDate> holidaySet = hariLiburRepository
                    .findByTanggalBetween(request.getTanggalMulai(), targetJune30)
                    .stream()
                    .map(TanggalHariLibur::getTanggal)
                    .collect(Collectors.toSet());
            int totalCutiJuni = WorkdayCalculator.count(request.getTanggalMulai(), targetJune30, holidaySet);
            res = CutiKuotaAllocator.allocate(totalHariCuti, prevKuota, currentKuota, totalCutiJuni);
        }

        CutiAllocationHelper.applyAllocation(entity, res);
    }
}
