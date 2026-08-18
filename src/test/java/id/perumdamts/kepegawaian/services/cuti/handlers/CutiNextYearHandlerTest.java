package id.perumdamts.kepegawaian.services.cuti.handlers;

import id.perumdamts.kepegawaian.dto.cuti.kuota.SisaCutiRecord;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.cuti.CutiPeriodClassifier;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * kepegawaian-ebt (sisi submission): CutiNextYearHandler harus mengalokasikan terhadap
 * pasangan tahun dari resolver (refYear, endYear), bukan startYear-1 — supaya sinkron
 * dengan settlement dan sesuai legacy "sisa tahun berjalan → tahun depan".
 */
@ExtendWith(MockitoExtension.class)
class CutiNextYearHandlerTest {

    @Mock private CutiKuotaRepository cutiKuotaRepository;
    @InjectMocks private CutiNextYearHandler handler;

    @Test
    void farFutureNextYearAllocatesAgainstRefYearAndEndYear() {
        CutiPengajuanPostRequest request = new CutiPengajuanPostRequest();
        request.setPegawaiId(7L);
        request.setTanggalMulai(LocalDate.of(2028, 1, 5));
        request.setTanggalSelesai(LocalDate.of(2028, 1, 9));
        request.setJumlahHariKerja(3);

        CutiPegawai entity = new CutiPegawai();
        entity.setJumlahHariKerja(3);

        when(cutiKuotaRepository.findRecordByPegawai_IdAndTahun(7L, 2026, SisaCutiRecord.class))
                .thenReturn(Optional.of(new SisaCutiRecord(30)));
        when(cutiKuotaRepository.findRecordByPegawai_IdAndTahun(7L, 2028, SisaCutiRecord.class))
                .thenReturn(Optional.of(new SisaCutiRecord(10)));

        handler.handle(request, entity, CutiPeriodClassifier.deriveYearPair(
                ECutiPeriod.NEXT_YEAR, request.getTanggalMulai(), request.getTanggalSelesai(), 2026));

        assertEquals(30, entity.getRiwayatKuota0());
        assertEquals(10, entity.getRiwayatKuota1());
        assertEquals(3, entity.getRiwayatPakai0() + entity.getRiwayatPakai1());
        // tahun 2027 (startYear-1) TIDAK boleh disentuh
        verify(cutiKuotaRepository, never()).findRecordByPegawai_IdAndTahun(eq(7L), eq(2027), any());
    }
}
