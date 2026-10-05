package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiRequest extends PagedRequest {
    private String search;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Long jabatanId;
    private Long organisasiId;
    private Long profesiId;
    private Long golonganId;
    private Long gradeId;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja = EStatusKerja.KARYAWAN_AKTIF;
    private EJenisKelamin jenisKelamin;
}
