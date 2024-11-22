package id.perumdamts.kepegawaian.entities.commons;

public enum EStatusKawin {
    BELUM_KAWIN("Belum Kawin"),
    KAWIN("Kawin"),
    JANDA_DUDA("Janda / Duda"),
    MENIKAH_SEKANTOR("Menikah Sekantor"),
    TIDAK_TAHU("Tidak Tahu");

    private final String label;

    EStatusKawin(String label) {
        this.label = label;
    }
}
