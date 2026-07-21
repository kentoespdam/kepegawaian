package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiRow;

import java.util.List;

public record JenisSpQuery(Long id, String kode, String nama, List<SanksiRow> sanksiList) {}
