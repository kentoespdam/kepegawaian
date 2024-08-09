package id.perumdamts.kepegawaian.entities.commons;

public enum EStatusPegawai {
    NONE("null"),
    KONTRAK("Pegawai Kontrak"),
    CAPEG("Calon Pegawai"),
    PEGAWAI("Pegawai Tetap"),
    CALON_HONORER("Calon Honorer Tetap"),
    HONORER("Honorer Tetap"),
    NON_PEGAWAI("Non Pegawai");

    public final String value;

    EStatusPegawai(String value) {
        this.value = value;
    }
}
