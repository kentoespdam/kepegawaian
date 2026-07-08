package id.perumdamts.kepegawaian.mapper.profil.keluarga;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EHubunganKeluarga;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPendidikan;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public final class ProfilKeluargaJooqMapper implements RecordMapper<Record, ProfilKeluargaQuery> {
    public static final ProfilKeluargaJooqMapper INSTANCE = new ProfilKeluargaJooqMapper();

    private ProfilKeluargaJooqMapper() {}

    @Override
    public ProfilKeluargaQuery map(Record record) {
        Long pendidikanId = record.get("pendidikan_id", Long.class);
        Long resolvedPendidikanId = Objects.requireNonNullElse(record.get("self_pendidikan_id", Long.class), pendidikanId);

        JenjangPendidikanResponse jenjang = null;
        if (pendidikanId != null) {
            jenjang = new JenjangPendidikanResponse(
                    pendidikanId,
                    record.get("pendidikan_nama", String.class),
                    record.get("pendidikan_short_name", String.class),
                    record.get("pendidikan_seq", Integer.class),
                    record.get("pendidikan_is_statistik", Boolean.class)
            );
        }

        return new ProfilKeluargaQuery(
                record.get("id", Long.class),
                record.get("biodata_id", String.class),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                record.get("nik", String.class),
                record.get("nama", String.class),
                toEnumName(record.get("jenis_kelamin", Byte.class), EJenisKelamin.class),
                toEnumName(record.get("agama", Byte.class), EAgama.class),
                toEnumName(record.get("hubungan_keluarga", Byte.class), EHubunganKeluarga.class),
                record.get("tempat_lahir", String.class),
                record.get("tanggal_lahir", java.time.LocalDate.class),
                record.get("tanggungan", Boolean.class),
                resolvedPendidikanId,
                jenjang,
                toEnumName(record.get("status_pendidikan", Byte.class), EStatusPendidikan.class),
                record.get("status_kawin", Boolean.class),
                record.get("notes", String.class),
                record.get("version", Integer.class),
                record.get("is_deleted", Boolean.class),
                record.get("changed_status", Boolean.class)
        );
    }

    private static <E extends Enum<E>> String toEnumName(Byte ordinal, Class<E> type) {
        if (ordinal == null) return null;
        E[] values = type.getEnumConstants();
        if (ordinal < 0 || ordinal >= values.length) return null;
        return values[ordinal].name();
    }
}
