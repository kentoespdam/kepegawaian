package id.perumdamts.kepegawaian.mapper.pegawai;

import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponseRingkasan;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.Record;
import org.jooq.Result;

import java.time.LocalDate;
import java.util.Objects;

public final class PegawaiRingkasanMapper {

    private PegawaiRingkasanMapper() {
    }

    public static PegawaiResponseRingkasan map(
            Record r,
            Result<? extends Record> kartuIdentitasResult
    ) {
        PegawaiResponseRingkasan response = new PegawaiResponseRingkasan();
        response.setId(r.get("id", Long.class));
        response.setNipam(r.get("nipam", String.class));
        response.setNama(r.get("nama", String.class));

        Byte jkByte = r.get("jenis_kelamin", Byte.class);
        if (jkByte != null) {
            response.setJenisKelamin(EJenisKelamin.values()[jkByte] == EJenisKelamin.LAKI_LAKI ? "Laki-Laki" : "Perempuan");
        }

        response.setTempatLahir(r.get("tempat_lahir", String.class));
        response.setTanggalLahir(r.get("tanggal_lahir", LocalDate.class));

        Byte skByte = r.get("status_kawin", Byte.class);
        if (skByte != null) {
            response.setStatusKawin(EStatusKawin.values()[skByte].toString());
        }

        response.setAlamat(r.get("alamat", String.class));
        response.setNik(r.get("nik", String.class));

        Byte agByte = r.get("agama", Byte.class);
        if (agByte != null) {
            response.setAgama(EAgama.values()[agByte].toString());
        }

        response.setTelp(r.get("telp", String.class));
        
        String emailVal = r.get("email", String.class);
        response.setEmail(Objects.nonNull(emailVal) ? emailVal : "");

        response.setKodePajak(r.get("kode_pajak", String.class));
        response.setIbuKandung(r.get("ibu_kandung", String.class));
        response.setPendidikanTerakhir(r.get("pendidikan_terakhir", String.class));

        response.setLembagaPendidikan(r.get("lembaga_pendidikan", String.class));
        response.setTahunLulus(r.get("tahun_lulus", Integer.class));

        Byte spByte = r.get("status_pegawai", Byte.class);
        if (spByte != null) {
            response.setStatusPegawai(EStatusPegawai.values()[spByte].value);
        }

        String pangkat = r.get("golongan_pangkat", String.class);
        String golongan = r.get("golongan_nama", String.class);
        if (Objects.nonNull(pangkat) && Objects.nonNull(golongan)) {
            response.setPangkatGolongan(pangkat + "-" + golongan);
        }
        
        response.setTmtGolongan(r.get("tmt_golongan", LocalDate.class));

        Integer mkgTahun = r.get("mkg_tahun", Integer.class);
        Integer mkgBulan = r.get("mkg_bulan", Integer.class);
        if (Objects.nonNull(mkgTahun) && Objects.nonNull(mkgBulan)) {
            response.setMkg(mkgTahun + " Tahun " + mkgBulan + " Bulan");
        }

        response.setUnitKerja(r.get("unit_kerja", String.class));
        response.setJabatan(r.get("jabatan", String.class));
        response.setProfesi(r.get("profesi", String.class));

        Integer gradeVal = r.get("grade_val", Integer.class);
        if (Objects.nonNull(gradeVal)) {
            response.setGrade("Grade " + gradeVal);
        }

        response.setTmtKerja(r.get("tmt_kerja", LocalDate.class));
        response.setTmtPegawai(r.get("tmt_pegawai", LocalDate.class));
        response.setTmtPensiun(r.get("tmt_pensiun", LocalDate.class));
        
        Boolean askesVal = r.get("is_askes", Boolean.class);
        response.setIsAskes(Objects.nonNull(askesVal) ? askesVal : false);

        Long absensiVal = r.get("absensi_id", Long.class);
        response.setAbsensiId(Objects.nonNull(absensiVal) ? absensiVal.intValue() : null);

        response.setNoKontrak("");
        response.setNoNpwp("");
        response.setNoJamsostek("");
        response.setNoBpjs("");
        response.setNoIdCard("");

        if (kartuIdentitasResult != null) {
            for (Record card : kartuIdentitasResult) {
                String jenisKartu = card.get("jenis_kartu_nama", String.class);
                String nomorKartu = card.get("nomor_kartu", String.class);
                if (Objects.nonNull(jenisKartu) && Objects.nonNull(nomorKartu)) {
                    if ("NPWP".equals(jenisKartu)) {
                        response.setNoNpwp(nomorKartu);
                    } else if ("JPn".equals(jenisKartu)) {
                        response.setNoJamsostek(nomorKartu);
                    } else if ("BPJS".equals(jenisKartu)) {
                        response.setNoBpjs(nomorKartu);
                    } else if ("ID Card".equals(jenisKartu)) {
                        response.setNoIdCard(nomorKartu);
                    }
                }
            }
        }

        return response;
    }
}
