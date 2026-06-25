package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDetail;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

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
                .fetchOptional()
                .map(r -> BiodataDetailRowMapper.map(
                        r,
                        r.get(pendidikanMultiset),
                        r.get(kartuIdentitasMultiset)));
    }
}
