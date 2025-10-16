package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public enum EJenisKenaikanBerkala {
    SK_KENAIKAN_PANGKAT_GOLONGAN("SK Kenaikan Pangkat/Gol"),
    SK_KENAIKAN_GAJI_BERKALA("SK Kenaikan Gaji Berkala");

    public final String value;

    EJenisKenaikanBerkala(String value) {
        this.value = value;
    }
}
