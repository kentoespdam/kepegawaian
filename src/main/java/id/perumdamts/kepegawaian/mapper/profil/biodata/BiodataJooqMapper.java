package id.perumdamts.kepegawaian.mapper.profil.biodata;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataQuery;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.jooq.enums.BiodataGolonganDarah;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class BiodataJooqMapper implements RecordMapper<Record, BiodataQuery> {
    public static final BiodataJooqMapper INSTANCE = new BiodataJooqMapper();

    private BiodataJooqMapper() {}

    @Override
    public BiodataQuery map(Record record) {
        Byte jkByte = record.get("jenis_kelamin", Byte.class);
        EJenisKelamin jenisKelamin = jkByte != null ? EJenisKelamin.values()[jkByte] : null;

        Byte agamaByte = record.get("agama", Byte.class);
        EAgama agama = agamaByte != null ? EAgama.values()[agamaByte] : null;

        Long ptId = record.get("pendidikan_terakhir_id", Long.class);
        Long selfPtId = record.get("self_pendidikan_terakhir_id", Long.class);
        Long resolvedPtId = selfPtId != null ? selfPtId : ptId;

        JenjangPendidikanResponse pendidikanTerakhir = null;
        if (ptId != null) {
            pendidikanTerakhir = new JenjangPendidikanResponse(
                    ptId,
                    record.get("pendidikan_terakhir_nama", String.class),
                    record.get("pendidikan_terakhir_short_name", String.class),
                    record.get("pendidikan_terakhir_seq", Integer.class),
                    record.get("pendidikan_terakhir_is_statistik", Boolean.class)
            );
        }

        BiodataGolonganDarah gd = record.get("golongan_darah", BiodataGolonganDarah.class);
        EGolonganDarah golonganDarah = gd != null ? EGolonganDarah.valueOf(gd.name()) : null;

        Byte skByte = record.get("status_kawin", Byte.class);
        EStatusKawin statusKawin = skByte != null ? EStatusKawin.values()[skByte] : null;

        return new BiodataQuery(
                record.get("nik", String.class),
                record.get("nama", String.class),
                jenisKelamin,
                record.get("tempat_lahir", String.class),
                record.get("tanggal_lahir", java.time.LocalDate.class),
                record.get("alamat", String.class),
                record.get("telp", String.class),
                agama,
                record.get("ibu_kandung", String.class),
                resolvedPtId,
                pendidikanTerakhir,
                golonganDarah,
                statusKawin,
                record.get("foto_profil", String.class),
                record.get("notes", String.class),
                record.get("is_pegawai", Boolean.class)
        );
    }
}
