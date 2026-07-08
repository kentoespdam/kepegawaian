package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

public record PegawaiMiniResponse(
        Long id,
        String nipam,
        String nama,
        String statusPegawai,
        String jabatan,
        String organisasi
) {}
