package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchMaster.GAJI_BATCH_MASTER;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

public final class GajiBatchMasterJooqMapper {
    private GajiBatchMasterJooqMapper() {}

    public static GajiBatchMasterResponse mapToResponse(Record record) {
        if (record == null) return null;

        return new GajiBatchMasterResponse(
                record.get(GAJI_BATCH_MASTER.ID),
                record.get(GAJI_BATCH_MASTER.BATCH_ROOT_ID),
                record.get(GAJI_BATCH_MASTER.PERIODE),
                record.get(GAJI_BATCH_MASTER.PEGAWAI_ID),
                record.get(GAJI_BATCH_MASTER.NIPAM),
                record.get(GAJI_BATCH_MASTER.NAMA),
                mapStatusPegawai(record),
                record.get(GAJI_BATCH_MASTER.ORGANISASI_ID),
                record.get(ORGANISASI.KODE),
                record.get(ORGANISASI.NAMA),
                record.get(GAJI_BATCH_MASTER.JABATAN_ID),
                record.get(GAJI_BATCH_MASTER.NAMA_JABATAN),
                record.get(GAJI_BATCH_MASTER.LEVEL_ID),
                record.get(GAJI_BATCH_MASTER.GOLONGAN_ID),
                record.get(GAJI_BATCH_MASTER.GOLONGAN),
                record.get(GAJI_BATCH_MASTER.GAJI_PROFIL_ID),
                record.get(GAJI_BATCH_MASTER.KODE_PAJAK),
                record.get(GAJI_BATCH_MASTER.GAJI_POKOK),
                record.get(GAJI_BATCH_MASTER.PHDP),
                mapStatusKawin(record),
                record.get(GAJI_BATCH_MASTER.JML_TANGGUNGAN),
                record.get(GAJI_BATCH_MASTER.JML_JIWA),
                record.get(GAJI_BATCH_MASTER.PENGHASILAN_KOTOR),
                record.get(GAJI_BATCH_MASTER.TOTAL_POTONGAN),
                record.get(GAJI_BATCH_MASTER.TOTAL_ADD_TAMBAHAN),
                record.get(GAJI_BATCH_MASTER.TOTAL_ADD_POTONGAN),
                record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH),
                record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH2),
                record.get(GAJI_BATCH_MASTER.PEMBULATAN),
                record.get(GAJI_BATCH_MASTER.PEMBULATAN2),
                record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH_FINAL),
                record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH_FINAL2),
                record.get(GAJI_BATCH_MASTER.PAJAK),
                Boolean.TRUE.equals(record.get(GAJI_BATCH_MASTER.IS_DIFFERENT))
        );
    }

    private static EStatusPegawai mapStatusPegawai(Record record) {
        var statusPegawai = record.get(GAJI_BATCH_MASTER.STATUS_PEGAWAI);
        if (statusPegawai != null) {
            return EStatusPegawai.values()[statusPegawai.intValue()];
        }
        return null;
    }

    private static EStatusKawin mapStatusKawin(Record record) {
        var statusKawin = record.get(GAJI_BATCH_MASTER.STATUS_KAWIN);
        if (statusKawin != null) {
            return EStatusKawin.values()[statusKawin.intValue()];
        }
        return null;
    }
}
