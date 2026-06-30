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
public class CutiOverlappingYearHandler implements CutiPeriodHandler {
    private final CutiKuotaRepository cutiKuotaRepository;

    @Override
    public void handle(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalDays = entity.getJumlahHariKerja();

        int currentYear = request.getTanggalMulai().getYear();
        int nextYear = request.getTanggalSelesai().getYear();

        int currentYearRemaining = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), currentYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Tahun Cuti Tidak Ditemukan"));
        int nextYearRemaining = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), nextYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);

        int totalRemaining = currentYearRemaining + nextYearRemaining;

        MinimalCutiRule.check(totalDays, totalRemaining);

        CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(totalDays, currentYearRemaining, nextYearRemaining);
        CutiAllocationHelper.applyAllocation(entity, res);
    }
}
