package id.perumdamts.kepegawaian.mapper.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanQuery;
import id.perumdamts.kepegawaian.mapper.master.SharedMappers;
import id.perumdamts.kepegawaian.repositories.master.jooq.JabatanSelects;
import id.perumdamts.kepegawaian.repositories.master.jooq.SharedSelects;
import org.jooq.Record;

public final class JabatanJooqMapper {
    private JabatanJooqMapper() {}

    public static JabatanQuery toQuery(Record record) {
        return new JabatanQuery(
                record.get(JabatanSelects.ID),
                record.get(JabatanSelects.KODE),
                record.get(JabatanSelects.NAMA),
                record.get(JabatanSelects.PARENT_ID) != null ? buildParent(record) : null,
                record.get(SharedSelects.ORG_ID) != null ? SharedMappers.buildOrganisasi(record) : null,
                record.get(SharedSelects.LEVEL_ID) != null ? SharedMappers.buildLevel(record) : null
        );
    }

    private static JabatanMiniResponse buildParent(Record record) {
        return new JabatanMiniResponse(
                record.get(JabatanSelects.PARENT_ID),
                record.get(JabatanSelects.PARENT_KODE),
                null,
                record.get(JabatanSelects.PARENT_NAMA));
    }
}
