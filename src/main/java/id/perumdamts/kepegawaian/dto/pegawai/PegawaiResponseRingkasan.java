package id.perumdamts.kepegawaian.dto.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class PegawaiResponseRingkasan {
    private Long id;
    private String nipam;
    private String nama;
    private String jenisKelamin;
    private String tempatLahir;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    private String statusKawin;
    private String alamat;
    private String nik;
    private String agama;
    private String telp;
    private String email = "";
    private String kodePajak;
    private String ibuKandung;
    private String pendidikanTerakhir;
    private String lembagaPendidikan;
    private Integer tahunLulus;
    private String statusPegawai;
    private String pangkatGolongan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtGolongan;
    private String mkg;
    private String unitKerja;
    private String jabatan;
    private String profesi;
    private String grade;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtKerja;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPegawai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPensiun;
    private Boolean isAskes=false;
    private Integer absensiId;
    private String noKontrak;
    private String noNpwp;
    private String noJamsostek;
    private String noBpjs;
    private String noIdCard;

    public static PegawaiResponseRingkasan from(Pegawai entity) {
        PegawaiResponseRingkasan response = new PegawaiResponseRingkasan();
        response.setId(entity.getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getBiodata().getNama());
        response.setJenisKelamin(entity.getBiodata().getJenisKelamin() == EJenisKelamin.LAKI_LAKI ? "Laki-Laki" : "Perempuan");
        response.setTempatLahir(entity.getBiodata().getTempatLahir());
        response.setTanggalLahir(entity.getBiodata().getTanggalLahir());
        response.setStatusKawin(entity.getBiodata().getStatusKawin().toString());
        response.setAlamat(entity.getBiodata().getAlamat());
        response.setNik(entity.getBiodata().getNik());
        response.setAgama(entity.getBiodata().getAgama().toString());
        response.setTelp(entity.getBiodata().getTelp());
        if (Objects.nonNull(entity.getKodePajak()))
            response.setKodePajak(entity.getKodePajak().getKode());
        response.setIbuKandung(entity.getBiodata().getIbuKandung());
        response.setPendidikanTerakhir(entity.getBiodata().getPendidikanTerakhir().getNama());
        if (!entity.getBiodata().getPendidikanList().isEmpty()) {
            Pendidikan pendidikan = entity.getBiodata().getPendidikanList()
                    .stream().filter(Pendidikan::getIsLatest)
                    .toList().getFirst();
            response.setLembagaPendidikan(pendidikan.getInstitusi());
            response.setTahunLulus(pendidikan.getTahunLulus());
        }
        response.setStatusPegawai(entity.getStatusPegawai().value);
        if (Objects.nonNull(entity.getGolongan())) {
            Golongan golongan = entity.getGolongan();
            response.setPangkatGolongan(golongan.getPangkat() + "-" + golongan.getGolongan());
            response.setTmtGolongan(entity.getTmtGolongan());
            response.setMkg(entity.getMkgTahun() + " Tahun " + entity.getMkgBulan() + " Bulan");
        }
        if (Objects.nonNull(entity.getOrganisasi()))
            response.setUnitKerja(entity.getOrganisasi().getNama());
        if (Objects.nonNull(entity.getJabatan()))
            response.setJabatan(entity.getJabatan().getNama());
        if (Objects.nonNull(entity.getProfesi()))
            response.setProfesi(entity.getProfesi().getNama());
        if (Objects.nonNull(entity.getGrade()))
            response.setGrade("Grade " + entity.getGrade().getGrade());
        response.setTmtKerja(entity.getTmtKerja());
        response.setTmtPegawai(entity.getTmtPegawai());
        response.setTmtPensiun(entity.getTmtPensiun());
        response.setIsAskes(entity.getIsAskes());
        if (!entity.getBiodata().getKartuIdentitasList().isEmpty()) {
            List<KartuIdentitas> list = entity.getBiodata().getKartuIdentitasList();
            response.setNoKontrak("");
            response.setNoNpwp(PegawaiResponseRingkasan.getIdNumber(list, "NPWP"));
            response.setNoJamsostek(PegawaiResponseRingkasan.getIdNumber(list, "JPn"));
            response.setNoBpjs(PegawaiResponseRingkasan.getIdNumber(list, "BPJS"));
            response.setNoIdCard(PegawaiResponseRingkasan.getIdNumber(list, "ID Card"));
        }
        return response;
    }

    private static String getIdNumber(List<KartuIdentitas> list, String jenisKartu) {
        List<KartuIdentitas> result = list.stream().filter(kartuIdentitas ->
                        kartuIdentitas.getJenisKartu().getNama().equals(jenisKartu))
                .toList();
        return result.isEmpty() ? "" : result.getFirst().getNomorKartu();
    }
}
