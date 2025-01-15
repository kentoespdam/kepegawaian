package id.perumdamts.kepegawaian.entities.commons;

@SuppressWarnings("ALL")
public enum EAgama {
    TIDAK_TAHU("Tidak Tahu"),
    ISLAM("Islam"),
    KRISTEN("Kristen"),
    KATOLIK("Katolik"),
    HINDU("Hindu"),
    BUDHA("Budha"),
    KONGHUCHU("Konghuchu"),
    ALIRAN_KEPERCAYAAN("Aliran Kepercayaan"),
    LAINNYA("Lainnya");

    private final String label;

    EAgama(String label) {
        this.label = label;
    }
}
