package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDetail;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class BiodataDetailQuery {
    private final DSLContext dsl;

    public Optional<BiodataDetail> getById(String nik) {
        var pendidikanMultiset = multiset(
                select(
                        PENDIDIKAN.ID,
                        PENDIDIKAN.BIODATA_ID,
                        PENDIDIKAN.GELAR_DEPAN,
                        PENDIDIKAN.GELAR_BELAKANG,
                        PENDIDIKAN.JURUSAN,
                        PENDIDIKAN.INSTITUSI,
                        PENDIDIKAN.KOTA,
                        PENDIDIKAN.TAHUN_MASUK,
                        PENDIDIKAN.IS_LULUS,
                        PENDIDIKAN.TAHUN_LULUS,
                        PENDIDIKAN.GPA,
                        PENDIDIKAN.IS_LATEST,
                        PENDIDIKAN.CHANGED_STATUS,
                        JENJANG_PENDIDIKAN.ID.as("jenjang_id"),
                        JENJANG_PENDIDIKAN.NAMA.as("jenjang_nama"),
                        JENJANG_PENDIDIKAN.SHORT_NAME.as("jenjang_short_name"),
                        JENJANG_PENDIDIKAN.SEQ.as("jenjang_seq"),
                        JENJANG_PENDIDIKAN.IS_STATISTIK.as("jenjang_is_statistik")
                ).from(PENDIDIKAN)
                        .leftJoin(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                        .where(PENDIDIKAN.BIODATA_ID.eq(nik))
                        .and(PENDIDIKAN.IS_DELETED.eq(false))
        ).as("pendidikan").convertFrom(r -> r.map(new PendidikanMultisetMapper()));

        var kartuIdentitasMultiset = multiset(
                select(
                        KARTU_IDENTITAS.ID,
                        KARTU_IDENTITAS.NIK.as("self_nik"),
                        KARTU_IDENTITAS.NOMOR_KARTU,
                        KARTU_IDENTITAS.TANGGAL_EXPIRED,
                        KARTU_IDENTITAS.TANGGAL_TERIMA,
                        KARTU_IDENTITAS.NOTES,
                        KARTU_IDENTITAS.CHANGED_STATUS,
                        JENIS_KITAS.ID.as("jenis_kartu_id"),
                        JENIS_KITAS.NAMA.as("jenis_kartu_nama")
                ).from(KARTU_IDENTITAS)
                        .leftJoin(JENIS_KITAS).on(KARTU_IDENTITAS.JENIS_KITAS_ID.eq(JENIS_KITAS.ID))
                        .where(KARTU_IDENTITAS.NIK.eq(nik))
                        .and(KARTU_IDENTITAS.IS_DELETED.eq(false))
        ).as("kartu_identitas").convertFrom(r -> r.map(new KartuIdentitasMultisetMapper()));

        return dsl.select(
                        BIODATA.NIK,
                        BIODATA.NAMA,
                        BIODATA.JENIS_KELAMIN,
                        BIODATA.TEMPAT_LAHIR,
                        BIODATA.TANGGAL_LAHIR,
                        BIODATA.ALAMAT,
                        BIODATA.TELP,
                        BIODATA.AGAMA,
                        BIODATA.IBU_KANDUNG,
                        BIODATA.PENDIDIKAN_ID,
                        BIODATA.GOLONGAN_DARAH,
                        BIODATA.STATUS_KAWIN,
                        BIODATA.FOTO_PROFIL,
                        BIODATA.NOTES,
                        BIODATA.IS_PEGAWAI,
                        pendidikanMultiset,
                        kartuIdentitasMultiset
                ).from(BIODATA)
                .where(BIODATA.NIK.eq(nik))
                .and(BIODATA.IS_DELETED.eq(false))
                .fetchOptionalInto(BiodataDetail.class);
    }

    private static class PendidikanMultisetMapper implements RecordMapper<Record, PendidikanQuery> {
        @Override
        public PendidikanQuery map(Record record) {
            PendidikanQuery q = new PendidikanQuery();
            q.setId(record.get("id", Long.class));
            q.setBiodataId(record.get("biodata_id", String.class));
            q.setGelarDepan(record.get("gelar_depan", String.class));
            q.setGelarBelakang(record.get("gelar_belakang", String.class));
            q.setJurusan(record.get("jurusan", String.class));
            q.setInstitusi(record.get("institusi", String.class));
            q.setKota(record.get("kota", String.class));
            q.setTahunMasuk(record.get("tahun_masuk", Integer.class));
            q.setIsLulus(record.get("is_lulus", Boolean.class));
            q.setTahunLulus(record.get("tahun_lulus", Integer.class));
            q.setGpa(record.get("gpa", Double.class));
            q.setIsLatest(record.get("is_latest", Boolean.class));
            q.setChangedStatus(record.get("changed_status", Byte.class));
            return q;
        }
    }

    private static class KartuIdentitasMultisetMapper implements RecordMapper<Record, KartuIdentitasQuery> {
        @Override
        public KartuIdentitasQuery map(Record record) {
            KartuIdentitasQuery q = new KartuIdentitasQuery();
            q.setId(record.get("id", Long.class));
            q.setBiodataId(record.get("self_nik", String.class));
            q.setNomorKartu(record.get("nomor_kartu", String.class));
            q.setTanggalExpired(record.get("tanggal_expired", java.time.LocalDate.class));
            q.setTanggalTerima(record.get("tanggal_terima", java.time.LocalDate.class));
            q.setNotes(record.get("notes", String.class));
            q.setChangedStatus(record.get("changed_status", Byte.class));
            return q;
        }
    }
}
