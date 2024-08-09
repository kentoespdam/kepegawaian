package id.perumdamts.kepegawaian.entities.commons;

public enum EStatusKawin {
    BELUM_KAWIN("Belum Kawin"),
    KAWIN("Kawin"),
    CERAI_HIDUP("Cerai Hidup"),
    CERAI_MATI("Cerai Mati");

    private final String label;

    EStatusKawin(String label) {
        this.label = label;
    }
}
