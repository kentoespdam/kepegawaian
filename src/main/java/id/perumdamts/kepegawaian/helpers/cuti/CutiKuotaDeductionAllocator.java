package id.perumdamts.kepegawaian.helpers.cuti;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaDeductionResult;

public class CutiKuotaDeductionAllocator {
    public static CutiKuotaDeductionResult deduct(int currentTerpakai, int currentSisa, int pakai) {
        return CutiKuotaDeductionResult.builder()
                .newKuotaTerpakai(currentTerpakai + pakai)
                .newSisaKuota(currentSisa - pakai)
                .build();
    }
}
