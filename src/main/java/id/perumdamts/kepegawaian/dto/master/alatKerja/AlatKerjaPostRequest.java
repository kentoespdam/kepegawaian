package id.perumdamts.kepegawaian.dto.master.alatKerja;

import jakarta.validation.constraints.NotEmpty;

public record AlatKerjaPostRequest(
        @NotEmpty(message = "Nama is required")
        String nama
) {}
