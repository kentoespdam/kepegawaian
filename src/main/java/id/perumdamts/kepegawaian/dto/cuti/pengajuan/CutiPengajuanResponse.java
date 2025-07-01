package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.cuti.jenis.JenisCutiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class CutiPengajuanResponse {
    private PegawaiResponse pegawai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalPengajuan;
    private EJenisPengajuanCuti jenisPengajuanCuti;
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

    public static CutiPengajuanResponse from(CutiPegawai entity) {
        CutiPengajuanResponse response = new CutiPengajuanResponse();
        response.setPegawai(PegawaiResponse.from(entity.getPegawai()));
        response.setTanggalPengajuan(entity.getCreatedAt().toLocalDate());
        response.setJenisPengajuanCuti(entity.getJenisPengajuanCuti());
        response.setJenisCuti(JenisCutiResponse.from(entity.getJenisCuti()));
        if (Objects.nonNull(entity.getSubJenisCuti()))
            response.setSubJenisCuti(JenisCutiResponse.from(entity.getSubJenisCuti()));
        response.setTanggalMulai(entity.getTanggalMulai());
        response.setTanggalSelesai(entity.getTanggalSelesai());
        response.setAlasan(entity.getAlasan());
        response.setJumlahHari(entity.getJumlahHari());
        response.setJumlahHariKerja(entity.getJumlahHariKerja());
        return response;
    }
}
