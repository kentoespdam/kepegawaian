package id.perumdamts.kepegawaian.mapper.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaDetail;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

public final class ProfilKeluargaDetailJooqMapper implements RecordMapper<Record, ProfilKeluargaDetail> {
    public static final ProfilKeluargaDetailJooqMapper INSTANCE = new ProfilKeluargaDetailJooqMapper();

    private ProfilKeluargaDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public ProfilKeluargaDetail map(Record record) {
        ProfilKeluargaQuery base = ProfilKeluargaJooqMapper.INSTANCE.map(record);
        return new ProfilKeluargaDetail(base, record.get("lampiran", List.class));
    }
}
