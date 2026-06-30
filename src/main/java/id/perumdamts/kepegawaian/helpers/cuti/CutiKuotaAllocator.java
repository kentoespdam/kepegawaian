package id.perumdamts.kepegawaian.helpers.cuti;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;

public class CutiKuotaAllocator {

    public static CutiKuotaAllocationResult allocate(int totalDays, int kuota0, int kuota1) {
        return allocate(totalDays, kuota0, kuota1, null);
    }

    public static CutiKuotaAllocationResult allocate(int totalDays, int kuota0, int kuota1, Integer maxUseKuota0) {
        int totalRemainingQuota = kuota0 + kuota1;
        if (totalRemainingQuota < totalDays) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        int pakai0;
        int pakai1;

        if (maxUseKuota0 != null) {
            int targetUse0 = Math.min(kuota0, maxUseKuota0);
            if (totalDays <= targetUse0) {
                pakai0 = totalDays;
                pakai1 = 0;
            } else {
                pakai0 = targetUse0;
                pakai1 = totalDays - pakai0;
            }
        } else {
            if (totalDays <= kuota0) {
                pakai0 = totalDays;
                pakai1 = 0;
            } else {
                pakai0 = kuota0;
                pakai1 = totalDays - kuota0;
            }
        }

        if (pakai1 > kuota1) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        return CutiKuotaAllocationResult.builder()
                .riwayatKuota0(kuota0)
                .riwayatPakai0(pakai0)
                .riwayatSisa0(kuota0 - pakai0)
                .riwayatKuota1(kuota1)
                .riwayatPakai1(pakai1)
                .riwayatSisa1(kuota1 - pakai1)
                .kuotaAwal(totalRemainingQuota)
                .kuotaAkhir(totalRemainingQuota - totalDays)
                .build();
    }
}
