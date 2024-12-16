package id.perumdamts.kepegawaian.dto.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class PegawaiResponse {
    private Long id;
    private String nipam;
    private BiodataMiniResponse biodata;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private OrganisasiResponse organisasi;
    private JabatanMiniResponse jabatan;
    private ProfesiResponse profesi;
    private GolonganResponse golongan;
    private GradeResponse grade;
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
    private GajiPendapatanNonPajakResponse kodePajak;
    private Boolean isAskes;

    private Integer mkgTahun;
    private Integer mkgBulan;

    private Long absensiId;
    private String notes;

    public static PegawaiResponse from(Pegawai pegawai) {
        PegawaiResponse response = new PegawaiResponse();
        response.setId(pegawai.getId());
        response.setNipam(pegawai.getNipam());
        response.setBiodata(BiodataMiniResponse.from(pegawai.getBiodata()));
        response.setStatusPegawai(pegawai.getStatusPegawai());
        response.setJabatan(JabatanMiniResponse.from(pegawai.getJabatan()));
        response.setOrganisasi(OrganisasiResponse.from(pegawai.getOrganisasi()));
        if (Objects.nonNull(pegawai.getProfesi()))
            response.setProfesi(ProfesiResponse.from(pegawai.getProfesi()));
        if (Objects.nonNull(pegawai.getGolongan()))
            response.setGolongan(GolonganResponse.from(pegawai.getGolongan()));
        if (Objects.nonNull(pegawai.getGrade()))
            response.setGrade(GradeResponse.from(pegawai.getGrade()));
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
        if (Objects.nonNull(pegawai.getKodePajak()))
            response.setKodePajak(GajiPendapatanNonPajakResponse.from(pegawai.getKodePajak()));
        response.setIsAskes(pegawai.getIsAskes());
        response.setMkgTahun(pegawai.getMkgTahun());
        response.setMkgBulan(pegawai.getMkgBulan());
        response.setAbsensiId(pegawai.getAbsensiId());
        response.setNotes(pegawai.getNotes());
        return response;
    }
}
