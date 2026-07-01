package id.perumdamts.kepegawaian.mapper.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaDetail;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

public final class ProfilKeluargaDetailJooqMapper implements RecordMapper<Record, ProfilKeluargaDetail> {
    public static final ProfilKeluargaDetailJooqMapper INSTANCE = new ProfilKeluargaDetailJooqMapper();

    private ProfilKeluargaDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public ProfilKeluargaDetail map(Record record) {
        ProfilKeluargaDetail detail = new ProfilKeluargaDetail();
        ProfilKeluargaQuery base = ProfilKeluargaJooqMapper.INSTANCE.map(record);
        detail.setId(base.getId());
        detail.setBiodataId(base.getBiodataId());
        detail.setBiodataNik(base.getBiodataNik());
        detail.setBiodataNama(base.getBiodataNama());
        detail.setNik(base.getNik());
        detail.setNama(base.getNama());
        detail.setJenisKelamin(base.getJenisKelamin());
        detail.setAgama(base.getAgama());
        detail.setHubunganKeluarga(base.getHubunganKeluarga());
        detail.setTempatLahir(base.getTempatLahir());
        detail.setTanggalLahir(base.getTanggalLahir());
        detail.setTanggungan(base.getTanggungan());
        detail.setPendidikanId(base.getPendidikanId());
        detail.setJenjangPendidikan(base.getJenjangPendidikan());
        detail.setStatusPendidikan(base.getStatusPendidikan());
        detail.setStatusKawin(base.getStatusKawin());
        detail.setNotes(base.getNotes());
        detail.setVersion(base.getVersion());
        detail.setIsDeleted(base.getIsDeleted());
        detail.setChangedStatus(base.getChangedStatus());
        detail.setLampiran(record.get("lampiran", List.class));
        return detail;
    }
}
