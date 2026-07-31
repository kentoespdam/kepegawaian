package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class PegawaiListRequest {
    private String search;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja = EStatusKerja.KARYAWAN_AKTIF;
}
