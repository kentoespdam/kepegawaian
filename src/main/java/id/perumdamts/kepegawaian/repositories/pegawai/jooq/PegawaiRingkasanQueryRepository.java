package id.perumdamts.kepegawaian.repositories.pegawai.jooq;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseRingkasan;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiRingkasanMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class PegawaiRingkasanQueryRepository {

    private final DSLContext dsl;

    public Optional<PegawaiResponseRingkasan> findRingkasan(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        var kartuIdentitasMultiset = multiset(
                select(
                        KARTU_IDENTITAS.NOMOR_KARTU,
                        JENIS_KITAS.NAMA.as("jenis_kartu_nama")
                ).from(KARTU_IDENTITAS)
                        .leftJoin(JENIS_KITAS).on(KARTU_IDENTITAS.JENIS_KITAS_ID.eq(JENIS_KITAS.ID))
                        .where(KARTU_IDENTITAS.NIK.eq(BIODATA.NIK))
                        .and(KARTU_IDENTITAS.IS_DELETED.eq(false))
        ).as("kartu_identitas");

        var riwayatSkMultiset = multiset(
                select(
                        RIWAYAT_SK.ID,
                        RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.JENIS_SK,
                        RIWAYAT_SK.TANGGAL_SK,
                        RIWAYAT_SK.TMT_BERLAKU
                ).from(RIWAYAT_SK)
                        .where(RIWAYAT_SK.PEGAWAI_ID.eq(PEGAWAI.ID))
                        .and(RIWAYAT_SK.IS_DELETED.eq(false))
        ).as("riwayat_sk");

        var opt = dsl.select(
                        PEGAWAI.ID.as("id"),
                        PEGAWAI.NIPAM.as("nipam"),
                        BIODATA.NAMA.as("nama"),
                        BIODATA.JENIS_KELAMIN.as("jenis_kelamin"),
                        BIODATA.TEMPAT_LAHIR.as("tempat_lahir"),
                        BIODATA.TANGGAL_LAHIR.as("tanggal_lahir"),
                        BIODATA.STATUS_KAWIN.as("status_kawin"),
                        BIODATA.ALAMAT.as("alamat"),
                        BIODATA.NIK.as("nik"),
                        BIODATA.AGAMA.as("agama"),
                        BIODATA.TELP.as("telp"),
                        PEGAWAI.EMAIL.as("email"),
                        GAJI_PENDAPATAN_NON_PAJAK.KODE.as("kode_pajak"),
                        BIODATA.IBU_KANDUNG.as("ibu_kandung"),
                        JENJANG_PENDIDIKAN.NAMA.as("pendidikan_terakhir"),
                        PENDIDIKAN.INSTITUSI.as("lembaga_pendidikan"),
                        PENDIDIKAN.TAHUN_LULUS.as("tahun_lulus"),
                        PEGAWAI.STATUS_PEGAWAI.as("status_pegawai"),
                        GOLONGAN.PANGKAT.as("golongan_pangkat"),
                        GOLONGAN.GOLONGAN_.as("golongan_nama"),
                        PEGAWAI.TMT_GOLONGAN.as("tmt_golongan"),
                        PEGAWAI.MKG_TAHUN.as("mkg_tahun"),
                        PEGAWAI.MKG_BULAN.as("mkg_bulan"),
                        ORGANISASI.NAMA.as("unit_kerja"),
                        JABATAN.NAMA.as("jabatan"),
                        PROFESI.NAMA.as("profesi"),
                        GRADE.GRADE_.as("grade_val"),
                        PEGAWAI.TMT_KERJA.as("tmt_kerja"),
                        PEGAWAI.TMT_PEGAWAI.as("tmt_pegawai"),
                        PEGAWAI.TMT_PENSIUN.as("tmt_pensiun"),
                        PEGAWAI.IS_ASKES.as("is_askes"),
                        PEGAWAI.ABSENSI_ID.as("absensi_id"),
                        kartuIdentitasMultiset,
                        riwayatSkMultiset
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(PENDIDIKAN).on(PENDIDIKAN.BIODATA_ID.eq(BIODATA.NIK)
                        .and(PENDIDIKAN.IS_LATEST.eq((byte) 1))
                        .and(PENDIDIKAN.IS_DELETED.eq(false)))
                .leftJoin(JENJANG_PENDIDIKAN).on(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(PROFESI).on(PEGAWAI.PROFESI_ID.eq(PROFESI.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(GRADE).on(PEGAWAI.GRADE_ID.eq(GRADE.ID))
                .leftJoin(GAJI_PENDAPATAN_NON_PAJAK).on(PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID.eq(GAJI_PENDAPATAN_NON_PAJAK.ID))
                .where(PEGAWAI.ID.eq(id).and(PEGAWAI.IS_DELETED.eq(false)))
                .fetchOptional();

        if (opt.isEmpty()) {
            return Optional.empty();
        }

        var record = opt.get();
        var cards = record.get(kartuIdentitasMultiset);
        
        PegawaiResponseRingkasan ringkasan = PegawaiRingkasanMapper.map(record, cards);
        return Optional.of(ringkasan);
    }
}
