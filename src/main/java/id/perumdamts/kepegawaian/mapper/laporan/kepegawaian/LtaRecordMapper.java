package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.LtaResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.ProfilKeluarga.PROFIL_KELUARGA;

public class LtaRecordMapper {
    public static LtaResponse map(Record r) {
        return new LtaResponse(
                r.get(PROFIL_KELUARGA.ID),
                r.get(PROFIL_KELUARGA.NAMA),
                r.get("jenis_kelamin", String.class),
                r.get(PROFIL_KELUARGA.TANGGAL_LAHIR),
                r.get("umur", Integer.class),
                r.get(PROFIL_KELUARGA.TANGGUNGAN) != null && r.get(PROFIL_KELUARGA.TANGGUNGAN) != 0,
                r.get("status_pendidikan", String.class),
                r.get("nama_karyawan", String.class),
                r.get(PEGAWAI.NIPAM),
                r.get("nama_jabatan", String.class)
        );
    }
}
