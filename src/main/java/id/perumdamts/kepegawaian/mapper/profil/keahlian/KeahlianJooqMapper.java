package id.perumdamts.kepegawaian.mapper.profil.keahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianResponse;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianQuery;
import id.perumdamts.kepegawaian.entities.commons.EKualifikasi;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class KeahlianJooqMapper implements RecordMapper<Record, KeahlianQuery> {
    public static final KeahlianJooqMapper INSTANCE = new KeahlianJooqMapper();

    private KeahlianJooqMapper() {}

    @Override
    public KeahlianQuery map(Record record) {
        Long jenisKeahlianId = record.get("jenis_keahlian_id", Long.class);
        JenisKeahlianResponse jk = null;
        if (jenisKeahlianId != null) {
            jk = new JenisKeahlianResponse(
                    jenisKeahlianId,
                    record.get("jenis_keahlian_nama", String.class)
            );
        }

        return new KeahlianQuery(
                record.get("id", Long.class),
                record.get("biodata_id", String.class),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                jk,
                mapKualifikasi(record.get("kualifikasi", Byte.class)),
                record.get("sertifikasi", Boolean.class),
                record.get("institusi", String.class),
                record.get("tahun", Integer.class),
                record.get("masa_berlaku", String.class),
                record.get("disetujui", Boolean.class),
                record.get("tanggal_pengajuan", java.time.LocalDateTime.class),
                record.get("tanggal_disetujui", java.time.LocalDateTime.class),
                record.get("disetujui_oleh", String.class),
                record.get("changed_status", Byte.class)
        );
    }

    private static String mapKualifikasi(Byte ordinal) {
        if (ordinal == null) return null;
        try {
            return EKualifikasi.values()[ordinal].name();
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
