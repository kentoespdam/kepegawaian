package id.perumdamts.kepegawaian.mapper.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanDetail;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Pelatihan.PELATIHAN;

public final class PelatihanDetailJooqMapper implements RecordMapper<Record, PelatihanDetail> {
    public static final PelatihanDetailJooqMapper INSTANCE = new PelatihanDetailJooqMapper();

    private PelatihanDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public PelatihanDetail map(Record record) {
        return new PelatihanDetail(
                record.get(PELATIHAN.ID),
                record.get(PELATIHAN.BIODATA_ID),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                record.get(PELATIHAN.JENIS_PELATIHAN_ID),
                record.get("jenis_pelatihan_nama", String.class),
                record.get(PELATIHAN.NAMA),
                record.get(PELATIHAN.LEMBAGA),
                record.get(PELATIHAN.TANGGAL_MULAI),
                record.get(PELATIHAN.TANGGAL_SELESAI),
                record.get(PELATIHAN.LULUS),
                record.get(PELATIHAN.NILAI),
                record.get(PELATIHAN.IKATAN_DINAS),
                record.get(PELATIHAN.TANGGAL_AKHIR_IKATAN),
                record.get(PELATIHAN.NOTES),
                record.get(PELATIHAN.CHANGED_STATUS),
                record.get("lampiran", List.class)
        );
    }
}
