package id.perumdamts.kepegawaian.dto.commons;

/**
 * Objek referensi generik bersarang: hanya id + label.
 * Dipakai read-tier ramping (mis. Session) di mana FE cuma butuh id untuk
 * shortcut-fetch dan nama untuk render. YAGNI — jangan tambah field kecuali
 * FE nyata butuh.
 */
public record RefMiniResponse(Long id, String nama) {
}
