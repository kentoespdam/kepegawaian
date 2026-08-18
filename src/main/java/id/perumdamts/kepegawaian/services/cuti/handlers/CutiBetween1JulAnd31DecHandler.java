package id.perumdamts.kepegawaian.services.cuti.handlers;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.SisaCutiRecord;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.cuti.CutiKuotaAllocator;
import id.perumdamts.kepegawaian.helpers.cuti.CutiPeriodClassifier;
import id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CutiBetween1JulAnd31DecHandler implements CutiPeriodHandler {
    private final CutiKuotaRepository cutiKuotaRepository;

    @Override
    public void handle(CutiPengajuanPostRequest request, CutiPegawai entity, CutiPeriodClassifier.YearPair pair) {
        int totalHariCuti = entity.getJumlahHariKerja();
        int year = pair.year1();

        int totalRemainingQuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        MinimalCutiRule.check(totalHariCuti, totalRemainingQuota);

        CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(totalHariCuti, totalRemainingQuota, 0);
        CutiAllocationHelper.applyAllocation(entity, res);
    }
}
