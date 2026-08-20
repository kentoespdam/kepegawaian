package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * kepegawaian-ebt: deduksi kuota saat approval final TIDAK boleh re-klasifikasi
 * dengan LocalDate.now() — cuti yang sama harus memotong kuota yang sama persis,
 * tidak peduli kapan approval-nya terjadi. Anchor: createdAt (tahun pengajuan).
 */
@ExtendWith(MockitoExtension.class)
class CutiKuotaUpdateByCutiServiceTest {

    @Mock private CutiKuotaRepository repository;
    @Mock private CutiProperties cutiProperties;
    @InjectMocks private CutiKuotaUpdateByCutiService service;

    private CutiPegawai cutiTahunan(LocalDate mulai, LocalDate selesai, LocalDateTime createdAt, int pakai0, int pakai1) {
        CutiJenis tahunan = new CutiJenis(1L);
        Pegawai pegawai = new Pegawai();
        pegawai.setId(7L);
        CutiPegawai cuti = new CutiPegawai();
        cuti.setJenisCuti(tahunan);
        cuti.setPegawai(pegawai);
        cuti.setTanggalMulai(mulai);
        cuti.setTanggalSelesai(selesai);
        cuti.setCreatedAt(createdAt);
        cuti.setRiwayatPakai0(pakai0);
        cuti.setRiwayatPakai1(pakai1);
        return cuti;
    }

    private CutiKuota kuota(int tahun, int terpakai, int sisa) {
        CutiKuota k = new CutiKuota();
        k.setTahun(tahun);
        k.setKuotaTerpakai(terpakai);
        k.setSisaKuota(sisa);
        return k;
    }

    @Test
    void overlappingCutiApprovedNextYearStillDeductsBothYears() {
        // Cuti 2025-12-30..2026-01-03 (diajukan Des 2025) yang approval final-nya terjadi
        // di tahun mana pun: dulu jatuh ke no-branch (silent no-op, kuota tidak dipotong).
        CutiPegawai cuti = cutiTahunan(
                LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 3),
                LocalDateTime.of(2025, 12, 10, 9, 0), 4, 2);
        CutiKuota kuota2025 = kuota(2025, 10, 30);
        CutiKuota kuota2026 = kuota(2026, 0, 12);

        when(cutiProperties.jenisCutiTahunan()).thenReturn(1L);
        when(repository.findByPegawai_IdAndTahun(7L, 2025)).thenReturn(Optional.of(kuota2025));
        when(repository.findByPegawai_IdAndTahun(7L, 2026)).thenReturn(Optional.of(kuota2026));

        service.updateKuota(cuti);

        // riwayatPakai0 (4) → kuota 2025, riwayatPakai1 (2) → kuota 2026
        assertEquals(14, kuota2025.getKuotaTerpakai());
        assertEquals(26, kuota2025.getSisaKuota());
        assertEquals(2, kuota2026.getKuotaTerpakai());
        assertEquals(10, kuota2026.getSisaKuota());
        verify(repository).save(kuota2025);
        verify(repository).save(kuota2026);
    }

    @Test
    void julDesCutiDeductsOnlyCurrentYear() {
        CutiPegawai cuti = cutiTahunan(
                LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 31),
                LocalDateTime.of(2026, 10, 1, 9, 0), 3, 0);
        CutiKuota kuota2026 = kuota(2026, 5, 20);

        when(cutiProperties.jenisCutiTahunan()).thenReturn(1L);
        when(repository.findByPegawai_IdAndTahun(7L, 2026)).thenReturn(Optional.of(kuota2026));

        service.updateKuota(cuti);

        assertEquals(8, kuota2026.getKuotaTerpakai());
        assertEquals(17, kuota2026.getSisaKuota());
        verify(repository).save(kuota2026);
    }

    @Test
    void farFutureNextYearCutiDeductsRefYearAndEndYearNotStartYearMinusOne() {
        // Cuti Jan 2028 diajukan 2026: dulu NEXT_YEAR memakai startYear-1 (2027) sebagai
        // tahun pertama — seharusnya tahun pengajuan (2026) sesuai legacy "sisa tahun berjalan".
        CutiPegawai cuti = cutiTahunan(
                LocalDate.of(2028, 1, 5), LocalDate.of(2028, 1, 9),
                LocalDateTime.of(2026, 6, 1, 9, 0), 2, 3);
        CutiKuota kuota2026 = kuota(2026, 10, 30);
        CutiKuota kuota2028 = kuota(2028, 0, 10);

        when(cutiProperties.jenisCutiTahunan()).thenReturn(1L);
        when(repository.findByPegawai_IdAndTahun(7L, 2026)).thenReturn(Optional.of(kuota2026));
        when(repository.findByPegawai_IdAndTahun(7L, 2028)).thenReturn(Optional.of(kuota2028));

        service.updateKuota(cuti);

        assertEquals(12, kuota2026.getKuotaTerpakai());
        assertEquals(28, kuota2026.getSisaKuota());
        assertEquals(3, kuota2028.getKuotaTerpakai());
        assertEquals(7, kuota2028.getSisaKuota());
        verify(repository, never()).findByPegawai_IdAndTahun(7L, 2027);
    }
}
