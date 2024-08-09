package id.perumdamts.kepegawaian.entities.commons;

public enum EStatusKerja {
    BERHENTI_OR_KELUAR("Berhenti / Keluar"),
    DIRUMAHKAN("Dirumahkan"),
    KARYAWAN_AKTIF("Karyawan Aktif");

    public final String value;

    EStatusKerja(String value) {
        this.value = value;
    }
}
