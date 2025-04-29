package id.perumdamts.kepegawaian.dto.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@Slf4j
public class PegawaiResponseDetail {
    private Long id;
    private String nipam;
    private BiodataResponse biodata;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private OrganisasiMiniResponse organisasi;
    private JabatanMiniResponse jabatan;
    private ProfesiMiniResponse profesi;
    private GolonganResponse golongan;
    private GradeResponse grade;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtKerja;
    private RiwayatSkResponse skCapeg;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPensiun;
    private RiwayatSkResponse skPegawai;
    private RiwayatSkResponse skGolongan;
    private RiwayatSkResponse skJabatan;
    private RiwayatSkResponse skMutasi;
    private RiwayatSkResponse skKontrak;
    private RiwayatSkResponse skGajiBerkala;
    private Double gajiPokok;
    private Double phdp;
    private Integer jmlTanggungan;
    private Integer mkgTahun;
    private Integer mkgBulan;
    private Long absensiId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtKontrakSelesai;
    private Boolean isAskes;
    private GajiPendapatanNonPajakResponse kodePajak;
    private GajiProfilResponse gajiProfil;
    private RumahDinasResponse rumahDinas;
    private String notes;

    public static PegawaiResponseDetail from(Pegawai pegawai, List<RiwayatSk> list) {
        PegawaiResponseDetail response = new PegawaiResponseDetail();
        response.setId(pegawai.getId());
        response.setNipam(pegawai.getNipam());
        response.setBiodata(BiodataResponse.from(pegawai.getBiodata()));
        response.setStatusPegawai(pegawai.getStatusPegawai());
        response.setOrganisasi(OrganisasiMiniResponse.from(pegawai.getOrganisasi()));
        if (Objects.nonNull(pegawai.getJabatan()))
            response.setJabatan(JabatanMiniResponse.from(pegawai.getJabatan()));
        if (Objects.nonNull(pegawai.getProfesi()))
            response.setProfesi(ProfesiMiniResponse.from(pegawai.getProfesi()));
        if (Objects.nonNull(pegawai.getGolongan()))
            response.setGolongan(GolonganResponse.from(pegawai.getGolongan()));
        if (Objects.nonNull(pegawai.getGrade()))
            response.setGrade(GradeResponse.from(pegawai.getGrade()));
        response.setStatusKerja(pegawai.getStatusKerja());
        response.setTmtKerja(pegawai.getTmtKerja());
        response.setTanggalSk(pegawai.getTmtPegawai());
        response.setSkCapeg(RiwayatSkResponse.getLastFromList(list, EJenisSk.SK_CAPEG));
        if (Objects.nonNull(response.getSkCapeg()))
            response.setTanggalSk(response.getSkCapeg().getTmtBerlaku());
        response.setSkPegawai(RiwayatSkResponse.getLastFromList(list, EJenisSk.SK_PEGAWAI_TETAP));
        response.setSkGolongan(RiwayatSkResponse.getLastFromList(list, EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN));
        response.setSkJabatan(RiwayatSkResponse.getLastFromList(list, EJenisSk.SK_JABATAN));
        response.setSkMutasi(RiwayatSkResponse.getLastFromList(list, EJenisSk.SK_MUTASI));
        response.setSkKontrak(RiwayatSkResponse.getLastFromList(list, EJenisSk.SK_LAINNYA));
        response.setSkGajiBerkala(RiwayatSkResponse.getLastFromList(list, EJenisSk.SK_KENAIKAN_GAJI_BERKALA));
        response.setTmtPensiun(pegawai.getTmtPensiun());
        response.setGajiPokok(pegawai.getGajiPokok());
        response.setPhdp(pegawai.getPhdp());
        response.setJmlTanggungan(pegawai.getJmlTanggungan());
        response.setMkgTahun(pegawai.getMkgTahun());
        response.setMkgBulan(pegawai.getMkgBulan());
        response.setAbsensiId(pegawai.getAbsensiId());
        response.setNotes(pegawai.getNotes());
        response.setIsAskes(pegawai.getIsAskes());
        if (pegawai.getKodePajak() != null)
            response.setKodePajak(GajiPendapatanNonPajakResponse.from(pegawai.getKodePajak()));
        if (pegawai.getGajiProfil() != null)
            response.setGajiProfil(GajiProfilResponse.from(pegawai.getGajiProfil()));
        if (pegawai.getRumahDinas() != null)
            response.setRumahDinas(RumahDinasResponse.from(pegawai.getRumahDinas()));
        return response;
    }
}
