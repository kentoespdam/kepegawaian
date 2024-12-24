package id.perumdamts.kepegawaian.entities.commons;

public enum EStatusPendidikan {
    BELUM_SEKOLAH("Belum Sekolah"),
    SEKOLAH("Sekolah"),
    SELESAI_SEKOLAH("Selesai Sekolah");

    public final String value;

    EStatusPendidikan(String value) {
        this.value = value;
    }
}
