package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PegawaiResponse {
    private Long id;
    private String nipam;
    private Biodata biodata;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Organisasi organisasi;
    private Jabatan jabatan;
    private Profesi profesi;
    private Golongan golongan;
    private Grade grade;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja;

    private Long refSkCapegId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtKerja;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPensiun;

    private Long refSkPegawaiId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPegawai;

    private Long refSkGolId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtGolongan;
    private Long refSkJabatanId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtJabatan;

    private Long refSkMutasiId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtMutasi;

    private Double gajiPokok;
    private Double phdp;
    private Integer jmlTanggungan;
    private KodePajak kodePajak;
    private Boolean isAskes;

    private Integer mkgTahun;
    private Integer mkgBulan;

    private String email;
    private Long absensiId;
    private String notes;

    public record Biodata(
            String nik,
            String nama,
            String gelarDepan,
            String gelarBelakang
    ) {}

    public record Organisasi(
            Long id,
            String nama
    ) {}

    public record Jabatan(
            Long id,
            String nama
    ) {}

    public record Profesi(
            Long id,
            String nama
    ) {}

    public record Golongan(
            Long id,
            String golongan,
            String pangkat
    ) {}

    public record Grade(
            Long id,
            Integer grade
    ) {}

    public record KodePajak(
            Long id,
            String nama,
            String kode
    ) {}

    public static PegawaiResponse from(Pegawai pegawai) {
        PegawaiResponse response = new PegawaiResponse();
        response.setId(pegawai.getId());
        response.setNipam(pegawai.getNipam());
        if (pegawai.getBiodata() != null) {
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
            response.setBiodata(new Biodata(
                    pegawai.getBiodata().getNik(),
                    pegawai.getBiodata().getNama(),
                    gelarDepan,
                    gelarBelakang
            ));
        }
        response.setStatusPegawai(pegawai.getStatusPegawai());
        if (pegawai.getJabatan() != null) {
            response.setJabatan(new Jabatan(
                    pegawai.getJabatan().getId(),
                    pegawai.getJabatan().getNama()
            ));
        }
        if (pegawai.getOrganisasi() != null) {
            response.setOrganisasi(new Organisasi(
                    pegawai.getOrganisasi().getId(),
                    pegawai.getOrganisasi().getNama()
            ));
        }
        if (pegawai.getProfesi() != null) {
            response.setProfesi(new Profesi(
                    pegawai.getProfesi().getId(),
                    pegawai.getProfesi().getNama()
            ));
        }
        if (pegawai.getGolongan() != null) {
            response.setGolongan(new Golongan(
                    pegawai.getGolongan().getId(),
                    pegawai.getGolongan().getGolongan(),
                    pegawai.getGolongan().getPangkat()
            ));
        }
        if (pegawai.getGrade() != null) {
            response.setGrade(new Grade(
                    pegawai.getGrade().getId(),
                    pegawai.getGrade().getGrade()
            ));
        }
        response.setStatusKerja(pegawai.getStatusKerja());
        response.setRefSkCapegId(pegawai.getRefSkCapegId());
        response.setTmtKerja(pegawai.getTmtKerja());
        response.setTmtPensiun(pegawai.getTmtPensiun());
        response.setRefSkPegawaiId(pegawai.getRefSkPegawaiId());
        response.setTmtPegawai(pegawai.getTmtPegawai());
        response.setRefSkGolId(pegawai.getRefSkGolId());
        response.setTmtGolongan(pegawai.getTmtGolongan());
        response.setRefSkJabatanId(pegawai.getRefSkJabatanId());
        response.setTmtJabatan(pegawai.getTmtJabatan());
        response.setRefSkMutasiId(pegawai.getRefSkMutasiId());
        response.setTmtMutasi(pegawai.getTmtMutasi());
        response.setGajiPokok(pegawai.getGajiPokok());
        response.setPhdp(pegawai.getPhdp());
        response.setJmlTanggungan(pegawai.getJmlTanggungan());
        if (pegawai.getKodePajak() != null) {
            response.setKodePajak(new KodePajak(
                    pegawai.getKodePajak().getId(),
                    pegawai.getKodePajak().getKode(), // map kode to nama as label for compatibility
                    pegawai.getKodePajak().getKode()
            ));
        }
        response.setIsAskes(pegawai.getIsAskes());
        response.setMkgTahun(pegawai.getMkgTahun());
        response.setMkgBulan(pegawai.getMkgBulan());
        response.setEmail(pegawai.getEmail());
        response.setAbsensiId(pegawai.getAbsensiId());
        response.setNotes(pegawai.getNotes());
        return response;
    }
}
