package id.perumdamts.kepegawaian.mapper.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiRow;
import org.jooq.Record;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;

public final class JenisSpJooqMapper {
    private JenisSpJooqMapper() {}

    @SuppressWarnings("unchecked")
    public static JenisSpQuery toQuery(Record record) {
        return new JenisSpQuery(
                record.get(JENIS_SP.ID),
                record.get(JENIS_SP.KODE),
                record.get(JENIS_SP.NAMA),
                (List<SanksiRow>) record.get("sanksi_list")
        );
    }
}
