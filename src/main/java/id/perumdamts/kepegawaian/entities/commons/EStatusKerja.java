package id.perumdamts.kepegawaian.entities.commons;

public enum EStatusKerja {
    BERHENTI_OR_KELUAR("Berhenti / Keluar"),
    DIRUMAHKAN("Dirumahkan"),
    KARYAWAN_AKTIF("Karyawan Aktif"),
    LAMARAN_BARU("Lamaran Baru"),
    TAHAP_SELEKSI("Tahap Seleksi"),
    DITERIMA("Diterima"),
    DIREKOMENDASIKAN("Direkomendasikan"),
    DITOLAK("Ditolak");

    public final String value;

    EStatusKerja(String value) {
        this.value = value;
    }
}
