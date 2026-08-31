package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikStatusPegawaiResponse(
        String statusPegawai,
        Integer total,
        Double persen
) {}
