package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.commons.RefMiniResponse;

/**
 * Read-tier paling ramping. Payload minim untuk di-cache FE sesaat setelah
 * login + kunci shortcut-fetch ke page (dashboard, data-pegawai, terminasi).
 * Tanpa multiset, tanpa gaji/SK. Lihat docs/context/language-pegawai.md.
 */
public record PegawaiResponseSession(
        Long id,
        String nipam,
        String nik,
        String nama,
        RefMiniResponse jabatan,
        RefMiniResponse organisasi
) {
}
