package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.commons.RefMiniResponse;

/**
 * Read-tier untuk form mutasi. Hanya 6 field yang dibutuhkan FE:
 * NIPAM, Nama, Golongan, Unit Kerja (Organisasi), Jabatan, Profesi.
 */
public record PegawaiResponseMutasiContext(
        Long id,
        String nipam,
        String nama,
        RefMiniResponse golongan,
        RefMiniResponse organisasi,
        RefMiniResponse jabatan,
        RefMiniResponse profesi
) {
}
