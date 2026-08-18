package id.perumdamts.kepegawaian.services.cuti.handlers;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.cuti.CutiPeriodClassifier;

public interface CutiPeriodHandler {
    /**
     * {@code pair} adalah satu-satunya sumber pasangan tahun kuota (kepegawaian-ebt):
     * setiap handler TIDAK boleh menghitung sendiri tahun dari tanggal — pakai pair
     * dari {@link CutiPeriodClassifier#deriveYearPair} supaya sinkron dengan settlement.
     */
    void handle(CutiPengajuanPostRequest request, CutiPegawai entity, CutiPeriodClassifier.YearPair pair);
}
