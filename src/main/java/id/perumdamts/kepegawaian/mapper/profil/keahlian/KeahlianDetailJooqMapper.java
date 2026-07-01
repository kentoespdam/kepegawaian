package id.perumdamts.kepegawaian.mapper.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianDetail;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianQuery;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

public final class KeahlianDetailJooqMapper implements RecordMapper<Record, KeahlianDetail> {
    public static final KeahlianDetailJooqMapper INSTANCE = new KeahlianDetailJooqMapper();

    private KeahlianDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public KeahlianDetail map(Record record) {
        KeahlianDetail detail = new KeahlianDetail();
        KeahlianQuery base = KeahlianJooqMapper.INSTANCE.map(record);
        detail.setId(base.getId());
        detail.setBiodataId(base.getBiodataId());
        detail.setBiodataNik(base.getBiodataNik());
        detail.setBiodataNama(base.getBiodataNama());
        detail.setJenisKeahlianId(base.getJenisKeahlianId());
        detail.setJenisKeahlian(base.getJenisKeahlian());
        detail.setKualifikasi(base.getKualifikasi());
        detail.setSertifikasi(base.getSertifikasi());
        detail.setInstitusi(base.getInstitusi());
        detail.setTahun(base.getTahun());
        detail.setMasaBerlaku(base.getMasaBerlaku());
        detail.setDisetujui(base.getDisetujui());
        detail.setTanggalPengajuan(base.getTanggalPengajuan());
        detail.setTanggalDisetujui(base.getTanggalDisetujui());
        detail.setDisetujuiOleh(base.getDisetujuiOleh());
        detail.setChangedStatus(base.getChangedStatus());
        detail.setLampiran(record.get("lampiran", List.class));
        return detail;
    }
}
