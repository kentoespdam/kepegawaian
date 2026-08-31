package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.util.List;

public record SoResponse(
        Long key,
        Long boss,
        Integer level,
        String jabatan,
        String name,
        String nik,
        List<SoResponse> subordinates
) {}
