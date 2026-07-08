package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseRingkasan;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;

import java.util.List;

public final class PegawaiReadMapper {

    private PegawaiReadMapper() {}

    public static PegawaiResponse toResponse(Pegawai pegawai) {
        if (pegawai == null) return null;
        return new PegawaiResponse(
                pegawai.getId(),
                pegawai.getNipam(),
                buildBiodata(pegawai),
                pegawai.getStatusPegawai(),
                pegawai.getOrganisasi() != null
                        ? new PegawaiResponse.Organisasi(
                        pegawai.getOrganisasi().getId(),
                        pegawai.getOrganisasi().getNama()
                ) : null,
                pegawai.getJabatan() != null
                        ? new PegawaiResponse.Jabatan(
                        pegawai.getJabatan().getId(),
                        pegawai.getJabatan().getNama()
                ) : null,
                pegawai.getProfesi() != null
                        ? new PegawaiResponse.Profesi(
                        pegawai.getProfesi().getId(),
                        pegawai.getProfesi().getNama()
                ) : null,
                pegawai.getGolongan() != null
                        ? new PegawaiResponse.Golongan(
                        pegawai.getGolongan().getId(),
                        pegawai.getGolongan().getGolongan(),
                        pegawai.getGolongan().getPangkat()
                ) : null,
                pegawai.getGrade() != null
                        ? new PegawaiResponse.Grade(
                        pegawai.getGrade().getId(),
                        pegawai.getGrade().getGrade()
                ) : null,
                pegawai.getStatusKerja(),
                pegawai.getRefSkCapegId(),
                pegawai.getTmtKerja(),
                pegawai.getTmtPensiun(),
                pegawai.getRefSkPegawaiId(),
                pegawai.getTmtPegawai(),
                pegawai.getRefSkGolId(),
                pegawai.getTmtGolongan(),
                pegawai.getRefSkJabatanId(),
                pegawai.getTmtJabatan(),
                pegawai.getRefSkMutasiId(),
                pegawai.getTmtMutasi(),
                pegawai.getGajiPokok(),
                pegawai.getPhdp(),
                pegawai.getJmlTanggungan(),
                pegawai.getKodePajak() != null
                        ? new PegawaiResponse.KodePajak(
                        pegawai.getKodePajak().getId(),
                        pegawai.getKodePajak().getKode(),
                        pegawai.getKodePajak().getKode()
                ) : null,
                pegawai.getIsAskes(),
                pegawai.getMkgTahun(),
                pegawai.getMkgBulan(),
                pegawai.getEmail(),
                pegawai.getAbsensiId(),
                pegawai.getNotes()
        );
    }

    public static PegawaiMiniResponse toMiniResponse(Pegawai pegawai) {
        if (pegawai == null) return null;
        return new PegawaiMiniResponse(
                pegawai.getId(),
                pegawai.getNipam(),
                pegawai.getBiodata() != null ? pegawai.getBiodata().getNama() : null,
                pegawai.getStatusPegawai() != null ? pegawai.getStatusPegawai().getValue() : null,
                pegawai.getJabatan() != null ? pegawai.getJabatan().getNama() : null,
                pegawai.getOrganisasi() != null ? pegawai.getOrganisasi().getNama() : null
        );
    }

    public static PegawaiResponseRingkasan toRingkasan(Pegawai pegawai) {
        if (pegawai == null) return null;

        String jenisKelamin = pegawai.getBiodata() != null && pegawai.getBiodata().getJenisKelamin() != null
                ? (pegawai.getBiodata().getJenisKelamin() == EJenisKelamin.LAKI_LAKI ? "Laki-Laki" : "Perempuan")
                : null;
        String statusKawin = pegawai.getBiodata() != null && pegawai.getBiodata().getStatusKawin() != null
                ? pegawai.getBiodata().getStatusKawin().toString()
                : null;
        String agama = pegawai.getBiodata() != null && pegawai.getBiodata().getAgama() != null
                ? pegawai.getBiodata().getAgama().toString()
                : null;

        String pangkatGolongan = null;
        if (pegawai.getGolongan() != null) {
            pangkatGolongan = pegawai.getGolongan().getPangkat() + "-" + pegawai.getGolongan().getGolongan();
        }

        String mkg = (pegawai.getMkgTahun() != null && pegawai.getMkgBulan() != null)
                ? pegawai.getMkgTahun() + " Tahun " + pegawai.getMkgBulan() + " Bulan"
                : null;

        String grade = pegawai.getGrade() != null ? "Grade " + pegawai.getGrade().getGrade() : null;

        // Pendidikan terakhir
        String lembagaPendidikan = null;
        Integer tahunLulus = null;
        String pendidikanTerakhir = pegawai.getBiodata() != null && pegawai.getBiodata().getPendidikanTerakhir() != null
                ? pegawai.getBiodata().getPendidikanTerakhir().getNama()
                : null;
        if (pegawai.getBiodata() != null && pegawai.getBiodata().getPendidikanList() != null
                && !pegawai.getBiodata().getPendidikanList().isEmpty()) {
            var pendidikan = pegawai.getBiodata().getPendidikanList().stream()
                    .filter(Pendidikan::getIsLatest)
                    .toList();
            if (!pendidikan.isEmpty()) {
                lembagaPendidikan = pendidikan.getFirst().getInstitusi();
                tahunLulus = pendidikan.getFirst().getTahunLulus();
            }
        }

        // Kartu identitas
        String noNpwp = "";
        String noJamsostek = "";
        String noBpjs = "";
        String noIdCard = "";
        if (pegawai.getBiodata() != null && pegawai.getBiodata().getKartuIdentitas() != null
                && !pegawai.getBiodata().getKartuIdentitas().isEmpty()) {
            var list = pegawai.getBiodata().getKartuIdentitas();
            noNpwp = getIdNumber(list, "NPWP");
            noJamsostek = getIdNumber(list, "JPn");
            noBpjs = getIdNumber(list, "BPJS");
            noIdCard = getIdNumber(list, "ID Card");
        }

        return new PegawaiResponseRingkasan(
                pegawai.getId(),
                pegawai.getNipam(),
                pegawai.getBiodata() != null ? pegawai.getBiodata().getNama() : null,
                jenisKelamin,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getTempatLahir() : null,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getTanggalLahir() : null,
                statusKawin,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getAlamat() : null,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getNik() : null,
                agama,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getTelp() : null,
                pegawai.getEmail() != null ? pegawai.getEmail() : "",
                pegawai.getKodePajak() != null ? pegawai.getKodePajak().getKode() : null,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getIbuKandung() : null,
                pendidikanTerakhir,
                lembagaPendidikan,
                tahunLulus,
                pegawai.getStatusPegawai() != null ? pegawai.getStatusPegawai().value : null,
                pangkatGolongan,
                pegawai.getTmtGolongan(),
                mkg,
                pegawai.getOrganisasi() != null ? pegawai.getOrganisasi().getNama() : null,
                pegawai.getJabatan() != null ? pegawai.getJabatan().getNama() : null,
                pegawai.getProfesi() != null ? pegawai.getProfesi().getNama() : null,
                grade,
                pegawai.getTmtKerja(),
                pegawai.getTmtPegawai(),
                pegawai.getTmtPensiun(),
                pegawai.getIsAskes() != null ? pegawai.getIsAskes() : false,
                pegawai.getAbsensiId() != null ? pegawai.getAbsensiId().intValue() : null,
                "", noNpwp, noJamsostek, noBpjs, noIdCard
        );
    }

    private static PegawaiResponse.Biodata buildBiodata(Pegawai pegawai) {
        if (pegawai.getBiodata() == null) return null;
        String gelarDepan = null;
        String gelarBelakang = null;
        if (pegawai.getBiodata().getPendidikanList() != null) {
            for (Pendidikan p : pegawai.getBiodata().getPendidikanList()) {
                if (Boolean.TRUE.equals(p.getIsLatest())) {
                    gelarDepan = p.getGelarDepan();
                    gelarBelakang = p.getGelarBelakang();
                    break;
                }
            }
        }
        return new PegawaiResponse.Biodata(
                pegawai.getBiodata().getNik(),
                pegawai.getBiodata().getNama(),
                gelarDepan,
                gelarBelakang
        );
    }

    private static String getIdNumber(List<KartuIdentitas> list, String jenisKartu) {
        return list.stream()
                .filter(k -> k.getJenisKartu() != null && jenisKartu.equals(k.getJenisKartu().getNama()))
                .map(KartuIdentitas::getNomorKartu)
                .findFirst()
                .orElse("");
    }
}
