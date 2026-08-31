package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikUmurResponse(
        Integer umur,
        Integer total,
        Double persen
) {}
