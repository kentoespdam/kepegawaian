package id.perumdamts.kepegawaian.entities.commons;

import lombok.Getter;

@Getter
public enum EJenisLibur {
    LIBUR_NASIONAL("Libur Nasional"),
    CUTI_BERSAMA("Cuti Bersama");

    public final String value;

    EJenisLibur(String value) {
        this.value = value;
    }
}
