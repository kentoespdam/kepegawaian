package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpSimple;

public record SanksiJenisSpList(
        Long id,
        String kode,
        String keterangan,
        JenisSpSimple jenisSp,
        Boolean potTkk,
        Integer jmlPotTkk,
        Boolean isPendingPangkat,
        Boolean isPendingGaji,
        Boolean isTurunPangkat,
        Boolean isTurunJabatan,
        Boolean isSuspension,
        Boolean isTerminateDh,
        Boolean isTerminateTh
) {}
