package id.perumdamts.kepegawaian.services.cuti.handlers;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.SisaCutiRecord;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.cuti.CutiKuotaAllocator;
import id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CutiBetween1JanAnd30JunHandler implements CutiPeriodHandler {
    private final CutiKuotaRepository cutiKuotaRepository;

    @Override
    public void handle(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalDays = entity.getJumlahHariKerja();

        int prevYear = request.getTanggalMulai().getYear() - 1;
        int currentYear = request.getTanggalMulai().getYear();

        int prevKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(request.getPegawaiId(), prevYear, request.getTanggalSelesai(), SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);
        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), currentYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + currentYear + " tidak tersedia!"));

        int totalRemainingQuota = currentKuota + prevKuota;
        MinimalCutiRule.check(totalDays, totalRemainingQuota);

        CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(totalDays, prevKuota, currentKuota);
        CutiAllocationHelper.applyAllocation(entity, res);
    }
}
