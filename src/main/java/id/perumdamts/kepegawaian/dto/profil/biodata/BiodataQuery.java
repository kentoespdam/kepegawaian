package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BiodataQuery {
    private String nik;
    private String nama;
    private EJenisKelamin jenisKelamin;
    private String tempatLahir;
    private LocalDate tanggalLahir;
    private String alamat;
    private String telp;
    private EAgama agama;
    private String ibuKandung;
    private Long pendidikanTerakhirId;
    private JenjangPendidikanResponse pendidikanTerakhir;
    private EGolonganDarah golonganDarah;
    private EStatusKawin statusKawin;
    private String fotoProfil;
    private String notes;
    private Boolean isPegawai;
}
