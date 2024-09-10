package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisMutasi {
    PENGANGKATAN_PERTAMA("Pengangkatan Pertama"),
    MUTASI_LOKER("Pindah Lokasi Kerja"),
    MUTASI_GOLONGAN("Perubahan Golongan"),
    MUTASI_JABATAN("Perubahan Jabatan"),
//    PERUBAHAN_GAJI("Perubahan Gaji"),
    TERMINASI("Terminasi");

    public final String value;

    EJenisMutasi(String value) {
        this.value = value;
    }
}
