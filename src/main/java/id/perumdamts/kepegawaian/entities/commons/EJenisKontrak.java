package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisKontrak {
    PERPANJANGAN("Perpanjangan Kontrak"),
    PENGANGKATAN("Pengangkatan Menjadi Karyawan");

    public final String value;

    EJenisKontrak(String value) {
        this.value = value;
    }
}
