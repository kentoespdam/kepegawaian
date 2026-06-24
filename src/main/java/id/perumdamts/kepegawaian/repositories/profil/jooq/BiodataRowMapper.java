package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataQuery;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.jooq.enums.BiodataGolonganDarah;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public class BiodataRowMapper implements RecordMapper<Record, BiodataQuery> {

    @Override
    public BiodataQuery map(Record record) {
        BiodataQuery q = new BiodataQuery();
        q.setNik(record.get("nik", String.class));
        q.setNama(record.get("nama", String.class));
        q.setJenisKelamin(record.get("jenis_kelamin", Byte.class) != null
                ? id.perumdamts.kepegawaian.entities.commons.EJenisKelamin.values()[record.get("jenis_kelamin", Byte.class)]
                : null);
        q.setTempatLahir(record.get("tempat_lahir", String.class));
        q.setTanggalLahir(record.get("tanggal_lahir", java.time.LocalDate.class));
        q.setAlamat(record.get("alamat", String.class));
        q.setTelp(record.get("telp", String.class));
        q.setAgama(record.get("agama", Byte.class) != null
                ? id.perumdamts.kepegawaian.entities.commons.EAgama.values()[record.get("agama", Byte.class)]
                : null);
        q.setIbuKandung(record.get("ibu_kandung", String.class));

        Long ptId = record.get("pendidikan_terakhir_id", Long.class);
        Long selfPtId = record.get("self_pendidikan_terakhir_id", Long.class);
        q.setPendidikanTerakhirId(Objects.requireNonNullElse(selfPtId, ptId));

        if (ptId != null) {
            JenjangPendidikanResponse jp = new JenjangPendidikanResponse();
            jp.setId(ptId);
            jp.setNama(record.get("pendidikan_terakhir_nama", String.class));
            jp.setShortName(record.get("pendidikan_terakhir_short_name", String.class));
            jp.setSeq(record.get("pendidikan_terakhir_seq", Integer.class));
            jp.setIsStatistik(record.get("pendidikan_terakhir_is_statistik", Boolean.class));
            q.setPendidikanTerakhir(jp);
        }

        BiodataGolonganDarah gd = record.get("golongan_darah", BiodataGolonganDarah.class);
        q.setGolonganDarah(gd != null ? EGolonganDarah.valueOf(gd.name()) : null);
        q.setStatusKawin(record.get("status_kawin", Byte.class) != null
                ? id.perumdamts.kepegawaian.entities.commons.EStatusKawin.values()[record.get("status_kawin", Byte.class)]
                : null);
        q.setFotoProfil(record.get("foto_profil", String.class));
        q.setNotes(record.get("notes", String.class));
        q.setIsPegawai(record.get("is_pegawai", Boolean.class));
        return q;
    }
}
