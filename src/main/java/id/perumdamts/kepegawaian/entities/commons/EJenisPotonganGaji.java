package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisPotonganGaji {
    POTONGAN_TKK("Potongan Tkk"),
    POTONGAN_TAMBAHAN("Potongan Tambahan");

    public final String value;

    EJenisPotonganGaji(String value) {
        this.value = value;
    }

}
