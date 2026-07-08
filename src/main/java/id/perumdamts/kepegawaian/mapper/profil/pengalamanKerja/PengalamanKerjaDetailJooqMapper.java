package id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaDetail;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

public final class PengalamanKerjaDetailJooqMapper implements RecordMapper<Record, PengalamanKerjaDetail> {
    public static final PengalamanKerjaDetailJooqMapper INSTANCE = new PengalamanKerjaDetailJooqMapper();

    private PengalamanKerjaDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public PengalamanKerjaDetail map(Record record) {
        return new PengalamanKerjaDetail(
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
                record.get("changed_status", Byte.class),
                record.get("lampiran", List.class)
        );
    }
}
