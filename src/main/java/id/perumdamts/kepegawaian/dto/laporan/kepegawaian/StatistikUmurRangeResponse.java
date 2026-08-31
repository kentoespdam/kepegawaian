package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikUmurRangeResponse(
        String range,
        Integer total,
        Double persen
) {}
