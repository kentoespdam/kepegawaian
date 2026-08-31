package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikAgamaResponse(
        String agama,
        Integer total,
        Double persen
) {}
