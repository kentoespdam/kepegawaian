package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;

public record SanksiQuery(
        Long id, String kode, String keterangan,
        JenisSpMiniResponse jenisSp,
        Boolean potTkk, Integer jmlPotTkk,
        Boolean isPendingPangkat, Boolean isPendingGaji,
        Boolean isTurunPangkat, Boolean isTurunJabatan,
        Boolean isSuspension, Boolean isTerminateDh, Boolean isTerminateTh
) {}
