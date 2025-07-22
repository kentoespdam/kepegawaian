package id.perumdamts.kepegawaian.entities.commons;

import lombok.Getter;

@Getter
public enum EApprovalCutiStatus {
    PENDING("Menunggu Persetujuan"),
    APPROVED("Disetujui"),
    CONFIRMED("Dikonfirmasi"),
    REJECTED("Ditolak"),
    CANCELED("Dibatalkan"),
    RETURNED("Dikembalikan");

    private final String label;

    EApprovalCutiStatus(String label) {
        this.label = label;
    }

    public int getValue() {
        return this.ordinal();
    }
}
