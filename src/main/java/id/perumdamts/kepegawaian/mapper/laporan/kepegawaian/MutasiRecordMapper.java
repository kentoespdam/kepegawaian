package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.MutasiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.RiwayatMutasi.RIWAYAT_MUTASI;

public class MutasiRecordMapper {
    public static MutasiResponse map(Record r) {
        Byte jmByte = r.get(RIWAYAT_MUTASI.JENIS_MUTASI);
        EJenisMutasi jenisMutasi = jmByte != null ? EJenisMutasi.values()[jmByte.intValue()] : null;

        return new MutasiResponse(
                jenisMutasi,
                r.get(RIWAYAT_MUTASI.NIPAM),
                r.get(RIWAYAT_MUTASI.NAMA),
                r.get(RIWAYAT_MUTASI.TMT_BERLAKU),
                r.get(RIWAYAT_MUTASI.NAMA_ORGANISASI_LAMA) != null ? r.get(RIWAYAT_MUTASI.NAMA_ORGANISASI_LAMA) : "",
                r.get(RIWAYAT_MUTASI.NAMA_JABATAN_LAMA) != null ? r.get(RIWAYAT_MUTASI.NAMA_JABATAN_LAMA) : "",
                r.get(RIWAYAT_MUTASI.NAMA_GOLONGAN) != null ? r.get(RIWAYAT_MUTASI.NAMA_GOLONGAN) : "",
                r.get(RIWAYAT_MUTASI.NAMA_ORGANISASI) != null ? r.get(RIWAYAT_MUTASI.NAMA_ORGANISASI) : "",
                r.get(RIWAYAT_MUTASI.NAMA_JABATAN) != null ? r.get(RIWAYAT_MUTASI.NAMA_JABATAN) : "",
                r.get(RIWAYAT_MUTASI.NAMA_GOLONGAN_LAMA) != null ? r.get(RIWAYAT_MUTASI.NAMA_GOLONGAN_LAMA) : "",
                r.get(RIWAYAT_MUTASI.NOTES) != null ? r.get(RIWAYAT_MUTASI.NOTES) : ""
        );
    }
}
