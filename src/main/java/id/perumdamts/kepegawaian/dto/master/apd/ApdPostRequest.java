package id.perumdamts.kepegawaian.dto.master.apd;

import jakarta.validation.constraints.NotEmpty;

public record ApdPostRequest(
        @NotEmpty(message = "Nama is required")
        String nama
) {}
