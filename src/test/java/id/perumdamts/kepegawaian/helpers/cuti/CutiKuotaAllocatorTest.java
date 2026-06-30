package id.perumdamts.kepegawaian.helpers.cuti;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CutiKuotaAllocatorTest {

    @Test
    public void testAllocateWithinFirstBucket() {
        CutiKuotaAllocationResult result = CutiKuotaAllocator.allocate(5, 8, 12);
        assertEquals(8, result.getRiwayatKuota0());
        assertEquals(5, result.getRiwayatPakai0());
        assertEquals(3, result.getRiwayatSisa0());
        assertEquals(12, result.getRiwayatKuota1());
        assertEquals(0, result.getRiwayatPakai1());
        assertEquals(12, result.getRiwayatSisa1());
        assertEquals(20, result.getKuotaAwal());
        assertEquals(15, result.getKuotaAkhir());
    }

    @Test
    public void testAllocateSpilloverToSecondBucket() {
        CutiKuotaAllocationResult result = CutiKuotaAllocator.allocate(10, 6, 12);
        assertEquals(6, result.getRiwayatKuota0());
        assertEquals(6, result.getRiwayatPakai0());
        assertEquals(0, result.getRiwayatSisa0());
        assertEquals(12, result.getRiwayatKuota1());
        assertEquals(4, result.getRiwayatPakai1());
        assertEquals(8, result.getRiwayatSisa1());
        assertEquals(18, result.getKuotaAwal());
        assertEquals(8, result.getKuotaAkhir());
    }

    @Test
    public void testAllocateJune30BoundaryCapped() {
        // Cuti crosses June 30 / July 1: totalDays = 5, kuota0 (previous year) = 4, maxUse0 (June days) = 2
        CutiKuotaAllocationResult result = CutiKuotaAllocator.allocate(5, 4, 10, 2);
        assertEquals(4, result.getRiwayatKuota0());
        assertEquals(2, result.getRiwayatPakai0()); // capped to 2
        assertEquals(2, result.getRiwayatSisa0());
        assertEquals(10, result.getRiwayatKuota1());
        assertEquals(3, result.getRiwayatPakai1());
        assertEquals(7, result.getRiwayatSisa1());
        assertEquals(14, result.getKuotaAwal());
        assertEquals(9, result.getKuotaAkhir());
    }

    @Test
    public void testAllocateInsufficientQuotaThrows() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            CutiKuotaAllocator.allocate(15, 4, 8);
        });
        assertTrue(exception.getMessage().contains("Kuota Cuti tidak tersedia"));
    }
}
