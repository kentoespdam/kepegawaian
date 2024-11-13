package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisGaji {
    NONE("-"),
    PEMASUKAN("Pemasukan"),
    POTONGAN("Potongan");

    public final String value;

    EJenisGaji(String value) {
        this.value = value;
    }
}
