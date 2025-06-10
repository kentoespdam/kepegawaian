package id.perumdamts.kepegawaian.entities.commons;

import lombok.Getter;

@Getter
public enum EStatusCuti {
    WAIT_APPROVAL("Menunggu Persetujuan"),
    APPROVED("Disetujui"),
    CONFIRMED("Dikonfirmasi"),
    REJECTED("Ditolak"),
    CANCELLED("Dibatalkan"),
    RETURNED("Dikembalikan");

    private final String label;

    EStatusCuti(String label) {
        this.label = label;
    }

}
