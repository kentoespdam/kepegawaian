package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisTunjangan {
    JABATAN("Tunjangan Jabatan"),
    KINERJA("Tunjangan Kegiatan Kerja"),
    BERAS("Tunjangan Beras"),
    AIR("Tunjangan Air");

    public final String value;

    EJenisTunjangan(String value) {
        this.value = value;
    }
}
