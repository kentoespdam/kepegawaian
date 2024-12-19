package id.perumdamts.kepegawaian.entities.commons;

public enum EProsesGaji {
    PENDING("PENDING"),
    PROSES("Proses Sedang Berjalan"),
    WAIT_VERIFICATION_PHASE_1("Menunggu Verifikasi Tahap 1"),
    WAIT_VERIFICATION_PHASE_2("Menunggu Verifikasi Tahap 2"),
    WAIT_APPROVAL("Menunggu Approval"),
    FINISHED("Selesai");


    public final String value;

    EProsesGaji(String value) {
        this.value = value;
    }
}
