package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record BiodataDashboardResponse(
        String nik,
        String nama,
        String jenisKelamin,
        String tempatLahir,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalLahir,
        String agama,
        String statusKawin,
        String alamat,
        String noTelp,
        String email,
        String kodePajak,
        String ibuKandung,
        PendidikanDashboard detailPendidikanTerakhir
) {
    public record PendidikanDashboard(
            String tingkat,
            String jurusan,
            String institusi,
            Integer tahunLulus
    ) {
    }
}
