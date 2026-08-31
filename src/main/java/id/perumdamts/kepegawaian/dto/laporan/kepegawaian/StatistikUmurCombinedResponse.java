package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.util.List;

public record StatistikUmurCombinedResponse(
        List<StatistikUmurResponse> umur,
        List<StatistikUmurRangeResponse> range
) {}
