package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record BiodataResponse(
        String nik,
        String nama,
        EJenisKelamin jenisKelamin,
        String tempatLahir,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalLahir,
        String alamat,
        String telp,
        EAgama agama,
        String ibuKandung,
        JenjangPendidikanResponse pendidikanTerakhir,
        EGolonganDarah golonganDarah,
        EStatusKawin statusKawin,
        String fotoProfil,
        String notes,
        List<KartuIdentitasMiniResponse> kartuIdentitas
) {
    public static BiodataResponse from(Biodata entity) {
        JenjangPendidikanResponse pendidikanTerakhir = JenjangPendidikanResponse.from(entity.getPendidikanTerakhir());
        List<KartuIdentitasMiniResponse> kartuIdentitas = Objects.isNull(entity.getKartuIdentitas()) ? null :
                KartuIdentitasMiniResponse.from(entity.getKartuIdentitas());
        return new BiodataResponse(
                entity.getNik(),
                entity.getNama(),
                entity.getJenisKelamin(),
                entity.getTempatLahir(),
                entity.getTanggalLahir(),
                entity.getAlamat(),
                entity.getTelp(),
                entity.getAgama(),
                entity.getIbuKandung(),
                pendidikanTerakhir,
                entity.getGolonganDarah(),
                entity.getStatusKawin(),
                entity.getFotoProfil(),
                entity.getNotes(),
                kartuIdentitas
        );
    }

    public static List<BiodataResponse> from(List<Biodata> entities) {
        return entities.stream().map(BiodataResponse::from).toList();
    }
}
