package id.perumdamts.kepegawaian.mapper.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianDetail;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianQuery;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

public final class KeahlianDetailJooqMapper implements RecordMapper<Record, KeahlianDetail> {
    public static final KeahlianDetailJooqMapper INSTANCE = new KeahlianDetailJooqMapper();

    private KeahlianDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public KeahlianDetail map(Record record) {
        KeahlianQuery base = KeahlianJooqMapper.INSTANCE.map(record);
        return new KeahlianDetail(base, record.get("lampiran", List.class));
    }
}
