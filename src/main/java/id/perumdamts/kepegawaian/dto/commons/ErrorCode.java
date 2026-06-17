package id.perumdamts.kepegawaian.dto.commons;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INTERNAL("Terjadi kesalahan internal"),
    UNKNOWN_BATCH("Batch tidak ditemukan"),
    DUPLICATE_BATCH("Batch sudah ada"),
    DB_ERROR("Gagal menyimpan data");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
