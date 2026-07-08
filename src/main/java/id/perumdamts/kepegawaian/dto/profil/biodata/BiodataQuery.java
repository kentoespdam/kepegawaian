package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;

import java.time.LocalDate;

public record BiodataQuery(
        String nik,
        String nama,
        EJenisKelamin jenisKelamin,
        String tempatLahir,
        LocalDate tanggalLahir,
        String alamat,
        String telp,
        EAgama agama,
        String ibuKandung,
        Long pendidikanTerakhirId,
        JenjangPendidikanResponse pendidikanTerakhir,
        EGolonganDarah golonganDarah,
        EStatusKawin statusKawin,
        String fotoProfil,
        String notes,
        Boolean isPegawai
) {}
