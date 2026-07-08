package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseRingkasan;
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
        Long id = r.get("id", Long.class);
        String nipam = r.get("nipam", String.class);
        String nama = r.get("nama", String.class);

        Byte jkByte = r.get("jenis_kelamin", Byte.class);
        String jenisKelamin = jkByte != null
                ? (EJenisKelamin.values()[jkByte] == EJenisKelamin.LAKI_LAKI ? "Laki-Laki" : "Perempuan")
                : null;

        String tempatLahir = r.get("tempat_lahir", String.class);
        LocalDate tanggalLahir = r.get("tanggal_lahir", LocalDate.class);

        Byte skByte = r.get("status_kawin", Byte.class);
        String statusKawin = skByte != null ? EStatusKawin.values()[skByte].toString() : null;

        String alamat = r.get("alamat", String.class);
        String nik = r.get("nik", String.class);

        Byte agByte = r.get("agama", Byte.class);
        String agama = agByte != null ? EAgama.values()[agByte].toString() : null;

        String telp = r.get("telp", String.class);

        String emailVal = r.get("email", String.class);
        String email = Objects.nonNull(emailVal) ? emailVal : "";

        String kodePajak = r.get("kode_pajak", String.class);
        String ibuKandung = r.get("ibu_kandung", String.class);
        String pendidikanTerakhir = r.get("pendidikan_terakhir", String.class);
        String lembagaPendidikan = r.get("lembaga_pendidikan", String.class);
        Integer tahunLulus = r.get("tahun_lulus", Integer.class);

        Byte spByte = r.get("status_pegawai", Byte.class);
        String statusPegawai = spByte != null ? EStatusPegawai.values()[spByte].value : null;

        String pangkat = r.get("golongan_pangkat", String.class);
        String golonganNama = r.get("golongan_nama", String.class);
        String pangkatGolongan = (Objects.nonNull(pangkat) && Objects.nonNull(golonganNama))
                ? pangkat + "-" + golonganNama
                : null;

        LocalDate tmtGolongan = r.get("tmt_golongan", LocalDate.class);

        Integer mkgTahun = r.get("mkg_tahun", Integer.class);
        Integer mkgBulan = r.get("mkg_bulan", Integer.class);
        String mkg = (Objects.nonNull(mkgTahun) && Objects.nonNull(mkgBulan))
                ? mkgTahun + " Tahun " + mkgBulan + " Bulan"
                : null;

        String unitKerja = r.get("unit_kerja", String.class);
        String jabatan = r.get("jabatan", String.class);
        String profesi = r.get("profesi", String.class);

        Integer gradeVal = r.get("grade_val", Integer.class);
        String grade = Objects.nonNull(gradeVal) ? "Grade " + gradeVal : null;

        LocalDate tmtKerja = r.get("tmt_kerja", LocalDate.class);
        LocalDate tmtPegawai = r.get("tmt_pegawai", LocalDate.class);
        LocalDate tmtPensiun = r.get("tmt_pensiun", LocalDate.class);

        Boolean askesVal = r.get("is_askes", Boolean.class);
        Boolean isAskes = Objects.nonNull(askesVal) ? askesVal : false;

        Long absensiVal = r.get("absensi_id", Long.class);
        Integer absensiId = Objects.nonNull(absensiVal) ? absensiVal.intValue() : null;

        String noNpwp = "";
        String noJamsostek = "";
        String noBpjs = "";
        String noIdCard = "";

        if (kartuIdentitasResult != null) {
            for (Record card : kartuIdentitasResult) {
                String jenisKartu = card.get("jenis_kartu_nama", String.class);
                String nomorKartu = card.get("nomor_kartu", String.class);
                if (Objects.nonNull(jenisKartu) && Objects.nonNull(nomorKartu)) {
                    if ("NPWP".equals(jenisKartu)) {
                        noNpwp = nomorKartu;
                    } else if ("JPn".equals(jenisKartu)) {
                        noJamsostek = nomorKartu;
                    } else if ("BPJS".equals(jenisKartu)) {
                        noBpjs = nomorKartu;
                    } else if ("ID Card".equals(jenisKartu)) {
                        noIdCard = nomorKartu;
                    }
                }
            }
        }

        return new PegawaiResponseRingkasan(
                id, nipam, nama, jenisKelamin, tempatLahir, tanggalLahir,
                statusKawin, alamat, nik, agama, telp, email, kodePajak, ibuKandung,
                pendidikanTerakhir, lembagaPendidikan, tahunLulus, statusPegawai,
                pangkatGolongan, tmtGolongan, mkg, unitKerja, jabatan, profesi, grade,
                tmtKerja, tmtPegawai, tmtPensiun, isAskes, absensiId,
                "", noNpwp, noJamsostek, noBpjs, noIdCard
        );
    }
}
