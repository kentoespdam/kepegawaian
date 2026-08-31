package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikGelarResponse(
        String jenjang,
        String gelar,
        Integer total,
        Double persen
) {}
