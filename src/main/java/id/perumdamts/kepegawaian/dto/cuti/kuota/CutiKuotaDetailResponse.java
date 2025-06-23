package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CutiKuotaDetailResponse {
    private Long id;
    private PegawaiResponse pegawai;
    private Integer tahun;
    private Integer kuota;
    private Integer kuotaTerpakai;
    private Integer kuotaTambahan;
    private Integer sisaKuota;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expired;

    public static CutiKuotaDetailResponse from(CutiKuota entity) {
        CutiKuotaDetailResponse response = new CutiKuotaDetailResponse();
        response.setId(entity.getId());
        response.setPegawai(PegawaiResponse.from(entity.getPegawai()));
        response.setTahun(entity.getTahun());
        response.setKuota(entity.getKuota());
        response.setKuotaTerpakai(entity.getKuotaTerpakai());
        response.setKuotaTambahan(entity.getKuotaTambahan());
        response.setSisaKuota(entity.getSisaKuota());
        response.setExpired(entity.getExpired());
        return response;
    }
}
