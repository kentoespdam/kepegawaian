package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikGolonganResponse(
        String golongan,
        String pangkat,
        Integer jmlL,
        Integer jmlP,
        Integer total,
        Double persen
) {}
