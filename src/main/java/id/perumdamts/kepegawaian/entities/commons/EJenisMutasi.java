package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisMutasi {
    PENGANGKATAN_PERTAMA("Pengangkatan Pertama"),
    MUTASI_LOKER("Pindah Lokasi Kerja"),
    MUTASI_JABATAN("Perubahan Jabatan"),
    MUTASI_GOLONGAN("Perubahan Golongan"),
    MUTASI_GAJI("Perubahan Gaji"),
    MUTASI_GAJI_BERKALA("Perubahan Gaji Berkala"),
    TERMINASI("Terminasi");

    public final String value;

    EJenisMutasi(String value) {
        this.value = value;
    }
}
