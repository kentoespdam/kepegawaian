package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.CutiKuota.CUTI_KUOTA;

public final class CutiKuotaJooqMapper {
    private CutiKuotaJooqMapper() {}

    public static CutiKuotaResponse mapToResponse(Record record) {
        if (record == null) return null;
        CutiKuotaResponse res = new CutiKuotaResponse();
        res.setId(record.get(CUTI_KUOTA.ID));
        res.setTahun(record.get(CUTI_KUOTA.TAHUN));
        res.setKuota(record.get(CUTI_KUOTA.KUOTA));
        res.setKuotaTerpakai(record.get(CUTI_KUOTA.KUOTA_TERPAKAI));
        res.setKuotaTambahan(record.get(CUTI_KUOTA.KUOTA_TAMBAHAN));
        res.setSisaKuota(record.get(CUTI_KUOTA.SISA_KUOTA));
        res.setExpired(record.get(CUTI_KUOTA.EXPIRED));
        
        if (record.get("pegawai_id") != null) {
            PegawaiMiniResponse peg = new PegawaiMiniResponse();
            peg.setId((Long) record.get("pegawai_id"));
            peg.setNipam((String) record.get("pegawai_nipam"));
            peg.setNama((String) record.get("pegawai_nama"));
            
            Object statusObj = record.get("pegawai_status");
            if (statusObj != null) {
                peg.setStatusPegawai(statusObj.toString());
            }
            peg.setJabatan((String) record.get("pegawai_jabatan"));
            peg.setOrganisasi((String) record.get("pegawai_organisasi"));
            res.setPegawai(peg);
        }
        return res;
    }
}
