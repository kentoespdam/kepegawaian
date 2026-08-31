package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikJenisKelaminResponse(
        String jenisKelamin,
        Integer total,
        Double persen
) {}
