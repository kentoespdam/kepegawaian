package id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaDetail;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;
import java.util.Map;

public final class PengalamanKerjaDetailJooqMapper implements RecordMapper<Record, PengalamanKerjaDetail> {
    public static final PengalamanKerjaDetailJooqMapper INSTANCE = new PengalamanKerjaDetailJooqMapper();

    private PengalamanKerjaDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public PengalamanKerjaDetail map(Record record) {
        Map<String, Object> map = record.intoMap();
        var detail = new PengalamanKerjaDetail();
        detail.setId((Long) map.get("id"));
        detail.setBiodataId((String) map.get("biodata_id"));
        detail.setBiodataNik((String) map.get("biodata_nik"));
        detail.setBiodataNama((String) map.get("biodata_nama"));
        detail.setNamaPerusahaan((String) map.get("nama_perusahaan"));
        detail.setTypePerusahaan((String) map.get("type_perusahaan"));
        detail.setJabatan((String) map.get("jabatan"));
        detail.setLokasi((String) map.get("lokasi"));
        detail.setTahunMasuk((Integer) map.get("tahun_masuk"));
        detail.setTahunKeluar((Integer) map.get("tahun_keluar"));
        detail.setNotes((String) map.get("notes"));
        detail.setChangedStatus((Byte) map.get("changed_status"));
        detail.setLampiran((List<LampiranRow>) map.get("lampiran"));
        return detail;
    }
}
