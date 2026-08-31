package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KontrakResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatKontrak.RIWAYAT_KONTRAK;

public class KontrakRecordMapper {
    public static KontrakResponse map(Record r) {
        return new KontrakResponse(
                r.get(PEGAWAI.NIPAM),
                r.get(BIODATA.NAMA),
                r.get(RIWAYAT_KONTRAK.NOMOR_KONTRAK),
                r.get("nama_organisasi", String.class),
                r.get("nama_jabatan", String.class),
                r.get(RIWAYAT_KONTRAK.TANGGAL_MULAI),
                r.get(RIWAYAT_KONTRAK.TANGGAL_SELESAI),
                r.get("sisa_tahun", Integer.class),
                r.get("sisa_bulan", Integer.class)
        );
    }
}
