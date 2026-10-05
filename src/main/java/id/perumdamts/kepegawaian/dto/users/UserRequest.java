package id.perumdamts.kepegawaian.dto.users;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends PagedRequest {
    private String nipam;
    private String nama;
    private EStatusKerja statusKerja = EStatusKerja.KARYAWAN_AKTIF;


}
