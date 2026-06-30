package id.perumdamts.kepegawaian.dto.cuti.kuota;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CutiKuotaAllocationResult {
    private int riwayatKuota0;
    private int riwayatPakai0;
    private int riwayatSisa0;
    private int riwayatKuota1;
    private int riwayatPakai1;
    private int riwayatSisa1;
    private int kuotaAwal;
    private int kuotaAkhir;
}
