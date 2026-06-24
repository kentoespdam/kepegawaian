package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;

import java.time.LocalDate;
import java.util.List;

public record BiodataDetail(
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
        EGolonganDarah golonganDarah,
        EStatusKawin statusKawin,
        String fotoProfil,
        String notes,
        Boolean isPegawai,
        List<PendidikanQuery> pendidikan,
        List<KartuIdentitasQuery> kartuIdentitas
) {
}
