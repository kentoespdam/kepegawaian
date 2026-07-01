package id.perumdamts.kepegawaian.mapper.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanDetail;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Pelatihan.PELATIHAN;

public final class PelatihanDetailJooqMapper implements RecordMapper<Record, PelatihanDetail> {
    public static final PelatihanDetailJooqMapper INSTANCE = new PelatihanDetailJooqMapper();

    private PelatihanDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public PelatihanDetail map(Record record) {
        PelatihanDetail d = new PelatihanDetail();
        d.setId(record.get(PELATIHAN.ID));
        d.setBiodataId(record.get(PELATIHAN.BIODATA_ID));
        d.setBiodataNik(record.get("biodata_nik", String.class));
        d.setBiodataNama(record.get("biodata_nama", String.class));
        d.setJenisPelatihanId(record.get(PELATIHAN.JENIS_PELATIHAN_ID));
        d.setJenisPelatihanNama(record.get("jenis_pelatihan_nama", String.class));
        d.setNama(record.get(PELATIHAN.NAMA));
        d.setLembaga(record.get(PELATIHAN.LEMBAGA));
        d.setTanggalMulai(record.get(PELATIHAN.TANGGAL_MULAI));
        d.setTanggalSelesai(record.get(PELATIHAN.TANGGAL_SELESAI));
        d.setLulus(record.get(PELATIHAN.LULUS));
        d.setNilai(record.get(PELATIHAN.NILAI));
        d.setIkatanDinas(record.get(PELATIHAN.IKATAN_DINAS));
        d.setTanggalAkhirIkatan(record.get(PELATIHAN.TANGGAL_AKHIR_IKATAN));
        d.setNotes(record.get(PELATIHAN.NOTES));
        d.setChangedStatus(record.get(PELATIHAN.CHANGED_STATUS));
        d.setLampiran(record.get("lampiran", List.class));
        return d;
    }
}
