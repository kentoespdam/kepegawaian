package id.perumdamts.kepegawaian.entities.commons;

public enum EJenisSk {
    NONE("null"),
    SK_KENAIKAN_PANGKAT_GOLONGAN("SK Kenaikan Pangkat/Gol"),
    SK_CAPEG("SK Capeg"),
    SK_PEGAWAI_TETAP("SK Pegawai Tetap"),
    SK_JABATAN("SK Jabatan"),
    SK_MUTASI("SK Mutasi"),
    SK_PENSIUN("SK Pensiun"),
    SK_LAINNYA("SK Lainnya"),
    SK_PENYESUAIAN_GAJI("SK Penyesuaian Gaji"),
    SK_KENAIKAN_GAJI_BERKALA("SK Kenaikan Gaji Berkala");

    public final String value;

    EJenisSk(String value) {
        this.value = value;
    }
}
