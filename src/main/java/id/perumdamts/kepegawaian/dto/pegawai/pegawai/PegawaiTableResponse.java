package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.commons.RefMiniResponse;

import java.time.LocalDate;

public record PegawaiTableResponse(
        Long id,
        String nipam,
        String nama,
        String jenisKelamin,
        LocalDate tanggalLahir,
        LocalDate tmtPensiun,
        String statusKawin,
        String kodePajak,
        Boolean isBpjs,
        String pangkatGolongan,
        String statusPegawai,
        RefMiniResponse organisasi,
        RefMiniResponse jabatan,
        RefMiniResponse profesi
) {
}
