package id.perumdamts.kepegawaian.entities.commons;

import lombok.Getter;

@Getter
public enum EJenisPengajuanCuti {
    PENGAJUAN_CUTI("Pengajuan Cuti"),
    KLAIM_CUTI("Klaim Cuti");
    public final String value;

    EJenisPengajuanCuti(String value) {
        this.value = value;
    }
}
