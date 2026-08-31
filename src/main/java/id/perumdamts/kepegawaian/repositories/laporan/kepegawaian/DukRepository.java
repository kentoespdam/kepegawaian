package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.DukRecordMapper;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;

@Repository
@RequiredArgsConstructor
public class DukRepository {
    private final DSLContext dsl;

    public List<DukResponse> fetch() {
        var mkTahun = DSL.field("TIMESTAMPDIFF(YEAR, {0}, NOW())", Integer.class, PEGAWAI.TMT_KERJA);
        var mkBulan = DSL.field("TIMESTAMPDIFF(MONTH, {0}, NOW())", Integer.class, PEGAWAI.TMT_KERJA);
        var usia = DSL.field("TIMESTAMPDIFF(YEAR, {0}, NOW())", Integer.class, BIODATA.TANGGAL_LAHIR);

        return dsl.select(
                        BIODATA.NAMA, PEGAWAI.NIPAM,
                        GOLONGAN.GOLONGAN_, GOLONGAN.PANGKAT, PEGAWAI.TMT_GOLONGAN,
                        JABATAN.NAMA.as("nama_jabatan"), PEGAWAI.TMT_JABATAN, PEGAWAI.TMT_KERJA,
                        mkTahun.as("mk_tahun"), mkBulan.as("mk_bulan"), usia.as("usia"),
                        PENDIDIKAN.JURUSAN, PENDIDIKAN.TAHUN_LULUS, JENJANG_PENDIDIKAN.NAMA.as("tingkat_pendidikan"),
                        PEGAWAI.STATUS_PEGAWAI
                )
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .join(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .join(PENDIDIKAN).on(BIODATA.NIK.eq(PENDIDIKAN.BIODATA_ID))
                .join(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(PEGAWAI.STATUS_KERJA.in(
                                (byte) EStatusKerja.DIRUMAHKAN.ordinal(),
                                (byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                        .and(PENDIDIKAN.IS_LATEST.eq((byte) 1))
                .orderBy(GOLONGAN.GOLONGAN_.desc(), PEGAWAI.TMT_GOLONGAN, PEGAWAI.STATUS_PEGAWAI, PEGAWAI.TMT_KERJA)
                .fetch(DukRecordMapper::map);
    }
}
