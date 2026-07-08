package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;

import java.time.LocalDate;
import java.util.Objects;

public record CutiPengajuanResponse(
        Long id,
        Long pegawaiId,
        String nama,
        String nipam,
        String pangkatGolongan,
        OrganisasiMiniResponse organisasi,
        JabatanMiniResponse jabatan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalPengajuan,
        EJenisPengajuanCuti jenisPengajuanCuti,
        EApprovalCutiStatus approvalCutiStatus,
        Integer approvalLevel,
        CutiJenisMiniResponse jenisCuti,
        CutiJenisMiniResponse subJenisCuti,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalMulai,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSelesai,
        String alasan,
        Integer jumlahHari,
        Integer jumlahHariKerja,
        JabatanMiniResponse picSaatIni,
        Boolean isClaimed,
        CutiPengajuanMiniResponse refCuti
) {
    public static CutiPengajuanResponse from(CutiPegawai entity) {
        if (Objects.isNull(entity)) return null;
        return new CutiPengajuanResponse(
                entity.getId(),
                entity.getPegawai().getId(),
                entity.getPegawai().getBiodata().getNama(),
                entity.getPegawai().getNipam(),
                entity.getPangkatGolongan(),
                OrganisasiMiniResponse.from(entity.getPegawai().getOrganisasi()),
                JabatanMiniResponse.from(entity.getPegawai().getJabatan()),
                entity.getCreatedAt().toLocalDate(),
                entity.getJenisPengajuanCuti(),
                entity.getApprovalCutiStatus(),
                entity.getApprovalLevel(),
                CutiJenisMiniResponse.from(entity.getJenisCuti()),
                Objects.nonNull(entity.getSubJenisCuti())
                        ? CutiJenisMiniResponse.from(entity.getSubJenisCuti())
                        : null,
                entity.getTanggalMulai(),
                entity.getTanggalSelesai(),
                entity.getAlasan(),
                entity.getJumlahHari(),
                entity.getJumlahHariKerja(),
                JabatanMiniResponse.from(entity.getPicSaatIni()),
                entity.getIsClaimed(),
                CutiPengajuanMiniResponse.from(entity.getRefCuti())
        );
    }
}
