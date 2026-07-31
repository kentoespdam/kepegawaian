package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiListRequest extends PagedRequest {
    private String search;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja = EStatusKerja.KARYAWAN_AKTIF;
}

