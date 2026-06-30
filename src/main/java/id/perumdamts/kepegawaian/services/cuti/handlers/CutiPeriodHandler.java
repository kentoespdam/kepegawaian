package id.perumdamts.kepegawaian.services.cuti.handlers;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;

public interface CutiPeriodHandler {
    void handle(CutiPengajuanPostRequest request, CutiPegawai entity);
}
