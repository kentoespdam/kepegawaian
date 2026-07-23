package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.commons.RefMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiTableResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.Record;

import java.util.Objects;

import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

public final class PegawaiTableRecordMapper {

    private PegawaiTableRecordMapper() {
    }

    public static PegawaiTableResponse mapTableResponse(Record r) {
        Long id = r.get(PEGAWAI.ID);
        String nipam = r.get(PEGAWAI.NIPAM);
        String nama = r.get("biodata_nama", String.class);

        Byte jkByte = r.get("biodata_jenis_kelamin", Byte.class);
        String jenisKelamin = jkByte != null
                ? (EJenisKelamin.values()[jkByte] == EJenisKelamin.LAKI_LAKI ? "Laki-Laki" : "Perempuan")
                : null;

        var tanggalLahir = r.get("biodata_tanggal_lahir", java.time.LocalDate.class);
        var tmtPensiun = r.get(PEGAWAI.TMT_PENSIUN);

        Byte skByte = r.get("biodata_status_kawin", Byte.class);
        String statusKawin = skByte != null ? EStatusKawin.values()[skByte].toString() : null;

        String kodePajak = r.get("kode_pajak", String.class);

        Boolean isAskes = r.get(PEGAWAI.IS_ASKES);
        Boolean isBpjs = Objects.nonNull(isAskes) ? isAskes : false;

        String pangkat = r.get("golongan_pangkat", String.class);
        String golonganNama = r.get("golongan_golongan", String.class);
        String pangkatGolongan = (Objects.nonNull(pangkat) && Objects.nonNull(golonganNama))
                ? pangkat + "-" + golonganNama
                : null;

        Byte spByte = r.get(PEGAWAI.STATUS_PEGAWAI);
        String statusPegawai = spByte != null ? EStatusPegawai.values()[spByte].value : null;

        Long orgId = r.get("organisasi_id", Long.class);
        RefMiniResponse organisasi = orgId != null
                ? new RefMiniResponse(orgId, r.get("organisasi_nama", String.class))
                : null;

        Long jabId = r.get("jabatan_id", Long.class);
        RefMiniResponse jabatan = jabId != null
                ? new RefMiniResponse(jabId, r.get("jabatan_nama", String.class))
                : null;

        Long profId = r.get("profesi_id", Long.class);
        RefMiniResponse profesi = profId != null
                ? new RefMiniResponse(profId, r.get("profesi_nama", String.class))
                : null;

        return new PegawaiTableResponse(
                id, nipam, nama, jenisKelamin, tanggalLahir, tmtPensiun,
                statusKawin, kodePajak, isBpjs, pangkatGolongan, statusPegawai,
                organisasi, jabatan, profesi
        );
    }
}
