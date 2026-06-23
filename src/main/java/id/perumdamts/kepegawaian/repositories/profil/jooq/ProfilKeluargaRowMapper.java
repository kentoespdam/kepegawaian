package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EHubunganKeluarga;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPendidikan;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public class ProfilKeluargaRowMapper implements RecordMapper<Record, ProfilKeluargaQuery> {

    @Override
    public ProfilKeluargaQuery map(Record record) {
        ProfilKeluargaQuery q = new ProfilKeluargaQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("biodata_id", String.class));
        q.setBiodataNik(record.get("biodata_nik", String.class));
        q.setBiodataNama(record.get("biodata_nama", String.class));
        q.setNik(record.get("nik", String.class));
        q.setNama(record.get("nama", String.class));

        q.setJenisKelamin(toEnumName(record.get("jenis_kelamin", Byte.class), EJenisKelamin.class));
        q.setAgama(toEnumName(record.get("agama", Byte.class), EAgama.class));
        q.setHubunganKeluarga(toEnumName(record.get("hubungan_keluarga", Byte.class), EHubunganKeluarga.class));
        q.setTempatLahir(record.get("tempat_lahir", String.class));
        q.setTanggalLahir(record.get("tanggal_lahir", java.time.LocalDate.class));
        q.setTanggungan(record.get("tanggungan", Boolean.class));

        Long pendidikanId = record.get("pendidikan_id", Long.class);
        q.setPendidikanId(Objects.requireNonNullElse(record.get("self_pendidikan_id", Long.class), pendidikanId));

        if (pendidikanId != null) {
            JenjangPendidikanResponse jp = new JenjangPendidikanResponse();
            jp.setId(pendidikanId);
            jp.setNama(record.get("pendidikan_nama", String.class));
            jp.setShortName(record.get("pendidikan_short_name", String.class));
            jp.setSeq(record.get("pendidikan_seq", Integer.class));
            jp.setIsStatistik(record.get("pendidikan_is_statistik", Boolean.class));
            q.setJenjangPendidikan(jp);
        }

        q.setStatusPendidikan(toEnumName(record.get("status_pendidikan", Byte.class), EStatusPendidikan.class));
        q.setStatusKawin(record.get("status_kawin", Boolean.class));
        q.setNotes(record.get("notes", String.class));
        q.setVersion(record.get("version", Integer.class));
        q.setIsDeleted(record.get("is_deleted", Boolean.class));
        q.setChangedStatus(record.get("changed_status", Boolean.class));
        return q;
    }

    private static <E extends Enum<E>> String toEnumName(Byte ordinal, Class<E> type) {
        if (ordinal == null) return null;
        E[] values = type.getEnumConstants();
        if (ordinal < 0 || ordinal >= values.length) return null;
        return values[ordinal].name();
    }
}