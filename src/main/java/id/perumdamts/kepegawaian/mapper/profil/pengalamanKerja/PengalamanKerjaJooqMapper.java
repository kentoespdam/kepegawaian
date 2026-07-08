package id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class PengalamanKerjaJooqMapper implements RecordMapper<Record, PengalamanKerjaQuery> {
    public static final PengalamanKerjaJooqMapper INSTANCE = new PengalamanKerjaJooqMapper();

    private PengalamanKerjaJooqMapper() {}
    @Override
    public PengalamanKerjaQuery map(Record record) {
        return new PengalamanKerjaQuery(
                record.get("id", Long.class),
                record.get("biodata_id", String.class),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                record.get("nama_perusahaan", String.class),
                record.get("type_perusahaan", String.class),
                record.get("jabatan", String.class),
                record.get("lokasi", String.class),
                record.get("tahun_masuk", Integer.class),
                record.get("tahun_keluar", Integer.class),
                record.get("notes", String.class),
                record.get("changed_status", Byte.class)
        );
    }
}
