package id.perumdamts.kepegawaian.services.cuti.handlers;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;

public final class CutiAllocationHelper {
    private CutiAllocationHelper() {}

    public static void applyAllocation(CutiPegawai entity, CutiKuotaAllocationResult res) {
        entity.setRiwayatKuota0(res.getRiwayatKuota0());
        entity.setRiwayatPakai0(res.getRiwayatPakai0());
        entity.setRiwayatSisa0(res.getRiwayatSisa0());
        entity.setRiwayatKuota1(res.getRiwayatKuota1());
        entity.setRiwayatPakai1(res.getRiwayatPakai1());
        entity.setRiwayatSisa1(res.getRiwayatSisa1());
        entity.setKuotaAwal(res.getKuotaAwal());
        entity.setKuotaAkhir(res.getKuotaAkhir());
    }
}
