package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.cuti.jenis.JenisCutiResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class CutiPengajuanResponse {
    private Long id;
    private Long pegawaiId;
    private String nama;
    private String nipam;
    private OrganisasiMiniResponse organisasi;
    private JabatanMiniResponse jabatan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalPengajuan;
    private EJenisPengajuanCuti jenisPengajuanCuti;
    private EApprovalCutiStatus approvalCutiStatus;
    private JenisCutiResponse jenisCuti;
    private JenisCutiResponse subJenisCuti;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private String alasan;
    private Integer jumlahHari;
    private Integer jumlahHariKerja;
    private JabatanMiniResponse picSaatIni;

    public static CutiPengajuanResponse from(CutiPegawai entity) {
        CutiPengajuanResponse response = new CutiPengajuanResponse();
        response.setId(entity.getId());
        response.setPegawaiId(entity.getPegawai().getId());
        response.setNipam(entity.getPegawai().getNipam());
        response.setNama(entity.getPegawai().getBiodata().getNama());
        response.setOrganisasi(OrganisasiMiniResponse.from(entity.getPegawai().getOrganisasi()));
        response.setJabatan(JabatanMiniResponse.from(entity.getPegawai().getJabatan()));
        response.setTanggalPengajuan(entity.getCreatedAt().toLocalDate());
        response.setJenisPengajuanCuti(entity.getJenisPengajuanCuti());
        response.setApprovalCutiStatus(entity.getApprovalCutiStatus());
        response.setJenisCuti(JenisCutiResponse.from(entity.getJenisCuti()));
        if (Objects.nonNull(entity.getSubJenisCuti()))
            response.setSubJenisCuti(JenisCutiResponse.from(entity.getSubJenisCuti()));
        response.setTanggalMulai(entity.getTanggalMulai());
        response.setTanggalSelesai(entity.getTanggalSelesai());
        response.setAlasan(entity.getAlasan());
        response.setJumlahHari(entity.getJumlahHari());
        response.setJumlahHariKerja(entity.getJumlahHariKerja());
        response.setPicSaatIni(JabatanMiniResponse.from(entity.getPicSaatIni()));
        return response;
    }
}
