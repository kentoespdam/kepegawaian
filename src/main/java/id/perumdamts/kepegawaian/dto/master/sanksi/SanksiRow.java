package id.perumdamts.kepegawaian.dto.master.sanksi;

/**
 * Row projection for the {@code sanksi_sp} table — used as nested list
 * inside {@link id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpQuery}
 * via JOOQ {@code multiset}.
 */
public record SanksiRow(Long id, String kode, String keterangan) {}
