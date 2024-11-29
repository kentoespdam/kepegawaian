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
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
public class PegawaiResponseDetail {
    private Long id;
    private String nipam;
    private BiodataMiniResponse biodata;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private OrganisasiMiniResponse organisasi;
    private JabatanMiniResponse jabatan;
    private ProfesiMiniResponse profesi;
    private GolonganResponse golongan;
    private GradeResponse grade;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja;
    private RiwayatSkResponse skCapeg;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPensiun;
    private RiwayatSkResponse skPegawai;
    private RiwayatSkResponse skGolongan;
    private RiwayatSkResponse skJabatan;
    private RiwayatSkResponse skMutasi;
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

    public static PegawaiResponseDetail from(Pegawai pegawai, List<RiwayatSkResponse> list) {
        PegawaiResponseDetail response = new PegawaiResponseDetail();
        response.setId(pegawai.getId());
        response.setNipam(pegawai.getNipam());
        response.setBiodata(BiodataMiniResponse.from(pegawai.getBiodata()));
        response.setStatusPegawai(pegawai.getStatusPegawai());
        response.setOrganisasi(OrganisasiMiniResponse.from(pegawai.getOrganisasi()));
        response.setJabatan(JabatanMiniResponse.from(pegawai.getJabatan()));
        response.setProfesi(ProfesiMiniResponse.from(pegawai.getProfesi()));
        if (Objects.nonNull(pegawai.getGolongan()))
            response.setGolongan(GolonganResponse.from(pegawai.getGolongan()));
        response.setGrade(GradeResponse.from(pegawai.getGrade()));
        response.setStatusKerja(pegawai.getStatusKerja());
        response.setTanggalSk(pegawai.getTmtPegawai());
        for (RiwayatSkResponse riwayatSkResponse : list) {
            if (riwayatSkResponse.getJenisSk().equals(EJenisSk.SK_CAPEG)) {
                response.setSkCapeg(riwayatSkResponse);
                response.setTanggalSk(riwayatSkResponse.getTmtBerlaku());
            }
            if (riwayatSkResponse.getJenisSk().equals(EJenisSk.SK_PEGAWAI_TETAP))
                response.setSkPegawai(riwayatSkResponse);
            if (riwayatSkResponse.getJenisSk().equals(EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN))
                response.setSkGolongan(riwayatSkResponse);
            if (riwayatSkResponse.getJenisSk().equals(EJenisSk.SK_JABATAN))
                response.setSkJabatan(riwayatSkResponse);
            if (riwayatSkResponse.getJenisSk().equals(EJenisSk.SK_MUTASI))
                response.setSkMutasi(riwayatSkResponse);

        }
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
