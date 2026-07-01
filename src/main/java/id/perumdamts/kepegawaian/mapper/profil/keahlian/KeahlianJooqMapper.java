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
        KeahlianQuery q = new KeahlianQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("biodata_id", String.class));
        q.setBiodataNik(record.get("biodata_nik", String.class));
        q.setBiodataNama(record.get("biodata_nama", String.class));
        q.setJenisKeahlianId(record.get("self_jenis_keahlian_id", Long.class));

        Long jenisKeahlianId = record.get("jenis_keahlian_id", Long.class);
        if (jenisKeahlianId != null) {
            JenisKeahlianResponse jk = new JenisKeahlianResponse();
            jk.setId(jenisKeahlianId);
            jk.setNama(record.get("jenis_keahlian_nama", String.class));
            q.setJenisKeahlian(jk);
        }

        q.setKualifikasi(mapKualifikasi(record.get("kualifikasi", Byte.class)));
        q.setSertifikasi(record.get("sertifikasi", Boolean.class));
        q.setInstitusi(record.get("institusi", String.class));
        q.setTahun(record.get("tahun", Integer.class));
        q.setMasaBerlaku(record.get("masa_berlaku", String.class));
        q.setDisetujui(record.get("disetujui", Boolean.class));
        q.setTanggalPengajuan(record.get("tanggal_pengajuan", java.time.LocalDateTime.class));
        q.setTanggalDisetujui(record.get("tanggal_disetujui", java.time.LocalDateTime.class));
        q.setDisetujuiOleh(record.get("disetujui_oleh", String.class));
        q.setChangedStatus(record.get("changed_status", Byte.class));
        return q;
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
