package id.perumdamts.kepegawaian.mapper.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiQuery;
import id.perumdamts.kepegawaian.repositories.master.jooq.OrganisasiSelects;
import org.jooq.Record;

public final class OrganisasiJooqMapper {
    private OrganisasiJooqMapper() {}

    public static OrganisasiQuery toQuery(Record record) {
        return new OrganisasiQuery(
            record.get(OrganisasiSelects.ID),
            record.get(OrganisasiSelects.KODE),
            record.get(OrganisasiSelects.LEVEL_ORG),
            record.get(OrganisasiSelects.NAMA),
            record.get(OrganisasiSelects.SHORT_NAME),
            record.get(OrganisasiSelects.CATEGORY),
            record.get(OrganisasiSelects.PARENT_ID) != null ? buildParent(record) : null
        );
    }

    private static OrganisasiMiniResponse buildParent(Record record) {
        return new OrganisasiMiniResponse(
                record.get(OrganisasiSelects.PARENT_ID),
                record.get(OrganisasiSelects.PARENT_KODE),
                record.get(OrganisasiSelects.PARENT_NAMA),
                record.get(OrganisasiSelects.PARENT_SHORT_NAME));
    }
}
