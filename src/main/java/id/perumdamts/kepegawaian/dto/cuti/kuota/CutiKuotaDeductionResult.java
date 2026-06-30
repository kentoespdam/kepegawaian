package id.perumdamts.kepegawaian.dto.cuti.kuota;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CutiKuotaDeductionResult {
    private int newKuotaTerpakai;
    private int newSisaKuota;
}
