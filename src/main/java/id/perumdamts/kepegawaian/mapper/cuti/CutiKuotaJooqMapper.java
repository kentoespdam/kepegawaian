package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.CutiKuota.CUTI_KUOTA;

public final class CutiKuotaJooqMapper {
    private CutiKuotaJooqMapper() {}

    public static CutiKuotaResponse mapToResponse(Record record) {
        if (record == null) return null;
        PegawaiMiniResponse pegawai = record.get("pegawai_id") != null
                ? new PegawaiMiniResponse(
                (Long) record.get("pegawai_id"),
                (String) record.get("pegawai_nipam"),
                (String) record.get("pegawai_nama"),
                record.get("pegawai_status", String.class),
                (String) record.get("pegawai_jabatan"),
                (String) record.get("pegawai_organisasi"))
                : null;
        return new CutiKuotaResponse(
                record.get(CUTI_KUOTA.ID),
                pegawai,
                record.get(CUTI_KUOTA.TAHUN),
                record.get(CUTI_KUOTA.KUOTA),
                record.get(CUTI_KUOTA.KUOTA_TERPAKAI),
                record.get(CUTI_KUOTA.KUOTA_TAMBAHAN),
                record.get(CUTI_KUOTA.SISA_KUOTA),
                record.get(CUTI_KUOTA.EXPIRED)
        );
    }
}
