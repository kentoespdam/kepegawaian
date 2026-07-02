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
        GajiBatchMasterResponse response = new GajiBatchMasterResponse();
        response.setId(record.get(GAJI_BATCH_MASTER.ID));
        response.setGajiBatchRootId(record.get(GAJI_BATCH_MASTER.BATCH_ROOT_ID));
        response.setPeriode(record.get(GAJI_BATCH_MASTER.PERIODE));
        response.setPegawaiId(record.get(GAJI_BATCH_MASTER.PEGAWAI_ID));
        response.setNipam(record.get(GAJI_BATCH_MASTER.NIPAM));
        response.setNama(record.get(GAJI_BATCH_MASTER.NAMA));

        var statusPegawai = record.get(GAJI_BATCH_MASTER.STATUS_PEGAWAI);
        if (statusPegawai != null) {
            response.setStatusPegawai(EStatusPegawai.values()[statusPegawai.intValue()]);
        }

        response.setOrganisasiId(record.get(GAJI_BATCH_MASTER.ORGANISASI_ID));
        response.setOrganisasiKode(record.get(ORGANISASI.KODE));
        response.setNamaOrganisasi(record.get(ORGANISASI.NAMA));

        response.setJabatanId(record.get(GAJI_BATCH_MASTER.JABATAN_ID));
        response.setNamaJabatan(record.get(GAJI_BATCH_MASTER.NAMA_JABATAN));
        response.setLevelId(record.get(GAJI_BATCH_MASTER.LEVEL_ID));
        response.setGolonganId(record.get(GAJI_BATCH_MASTER.GOLONGAN_ID));
        response.setGolongan(record.get(GAJI_BATCH_MASTER.GOLONGAN));
        response.setGajiProfilId(record.get(GAJI_BATCH_MASTER.GAJI_PROFIL_ID));
        response.setKodePajak(record.get(GAJI_BATCH_MASTER.KODE_PAJAK));
        response.setGajiPokok(record.get(GAJI_BATCH_MASTER.GAJI_POKOK));
        response.setPhdp(record.get(GAJI_BATCH_MASTER.PHDP));

        var statusKawin = record.get(GAJI_BATCH_MASTER.STATUS_KAWIN);
        if (statusKawin != null) {
            response.setStatusKawin(EStatusKawin.values()[statusKawin.intValue()]);
        }

        response.setJmlTanggungan(record.get(GAJI_BATCH_MASTER.JML_TANGGUNGAN));
        response.setJmlJiwa(record.get(GAJI_BATCH_MASTER.JML_JIWA));
        response.setPenghasilanKotor(record.get(GAJI_BATCH_MASTER.PENGHASILAN_KOTOR));
        response.setTotalPotongan(record.get(GAJI_BATCH_MASTER.TOTAL_POTONGAN));
        response.setTotalAddTambahan(record.get(GAJI_BATCH_MASTER.TOTAL_ADD_TAMBAHAN));
        response.setTotalAddPotongan(record.get(GAJI_BATCH_MASTER.TOTAL_ADD_POTONGAN));
        response.setPenghasilanBersih(record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH));
        response.setPenghasilanBersih2(record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH2));
        response.setPembulatan(record.get(GAJI_BATCH_MASTER.PEMBULATAN));
        response.setPembulatan2(record.get(GAJI_BATCH_MASTER.PEMBULATAN2));
        response.setPenghasilanBersihFinal(record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH_FINAL));
        response.setPenghasilanBersihFinal2(record.get(GAJI_BATCH_MASTER.PENGHASILAN_BERSIH_FINAL2));
        response.setPajak(record.get(GAJI_BATCH_MASTER.PAJAK));
        response.setIsDifferent(Boolean.TRUE.equals(record.get(GAJI_BATCH_MASTER.IS_DIFFERENT)));

        return response;
    }
}
