package id.perumdamts.kepegawaian.helpers.cuti;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaDeductionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CutiKuotaDeductionAllocatorTest {

    @Test
    public void testDeduct() {
        CutiKuotaDeductionResult result = CutiKuotaDeductionAllocator.deduct(4, 8, 3);
        assertEquals(7, result.getNewKuotaTerpakai());
        assertEquals(5, result.getNewSisaKuota());
    }
}
