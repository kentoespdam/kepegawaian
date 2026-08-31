package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KenaikanBerkalaResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;

public class KenaikanBerkalaRecordMapper {
    public static KenaikanBerkalaResponse map(Record r) {
        Boolean isPendingGaji = r.get("is_pending_gaji", Boolean.class);
        Boolean isPendingPangkat = r.get("is_pending_pangkat", Boolean.class);

        return new KenaikanBerkalaResponse(
                r.get(RIWAYAT_SK.ID),
                r.get(PEGAWAI.ID),
                r.get(PEGAWAI.NIPAM),
                r.get(BIODATA.NAMA),
                r.get(RIWAYAT_SK.JENIS_SK),
                r.get(RIWAYAT_SK.NOMOR_SK),
                r.get(RIWAYAT_SK.TMT_BERLAKU),
                r.get(RIWAYAT_SK.KENAIKAN_BERIKUTNYA),
                r.get("tanggal_eksekusi_sanksi", java.time.LocalDate.class),
                isPendingGaji != null && isPendingGaji,
                isPendingPangkat != null && isPendingPangkat,
                r.get(JABATAN.NAMA),
                r.get(PEGAWAI.TMT_JABATAN),
                r.get(GOLONGAN.GOLONGAN_),
                r.get(GOLONGAN.PANGKAT),
                r.get(PEGAWAI.TMT_GOLONGAN),
                r.get("mkg_tahun", Integer.class),
                r.get("mkg_bulan", Integer.class),
                r.get(PEGAWAI.TMT_KERJA),
                r.get("mk_tahun", Integer.class),
                r.get("mk_bulan", Integer.class),
                r.get("pendidikan_terakhir", String.class),
                r.get(BIODATA.TEMPAT_LAHIR),
                r.get(BIODATA.TANGGAL_LAHIR)
        );
    }
}
