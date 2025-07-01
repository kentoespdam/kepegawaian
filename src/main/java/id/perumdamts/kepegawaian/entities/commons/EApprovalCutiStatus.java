package id.perumdamts.kepegawaian.entities.commons;

import lombok.Getter;

@Getter
public enum EApprovalCutiStatus {
    PENDING("Menunggu Persetujuan"),
    APPROVED("Disetujui"),
    CONFIRMED("Dikonfirmasi"),
    REJECTED("Ditolak"),
    CANCELLED("Dibatalkan"),
    RETURNED("Dikembalikan");

    private final String label;

    EApprovalCutiStatus(String label) {
        this.label = label;
    }
}
