package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisMutasi {
    PENGANGKATAN_PERTAMA("Pengangkatan Pertama"),
    PINDAH_LOKER_JABATAN("Pindah Loker / Jabatan"),
    PERUBAHAN_GAJI("Perubahan Gaji"),
    TERMINASI("Terminasi");

    public final String value;

    EJenisMutasi(String value) {
        this.value = value;
    }
}
