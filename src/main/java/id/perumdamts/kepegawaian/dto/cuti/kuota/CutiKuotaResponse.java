package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiMapper;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CutiKuotaResponse {
    private Long id;
    private PegawaiMiniResponse pegawai;
    private Integer tahun;
    private Integer kuota;
    private Integer kuotaTerpakai;
    private Integer kuotaTambahan;
    private Integer sisaKuota;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expired;

    public static CutiKuotaResponse from(CutiKuota entity) {
        CutiKuotaResponse response = new CutiKuotaResponse();
        response.setId(entity.getId());
        response.setPegawai(PegawaiMapper.toMiniResponse(entity.getPegawai()));
        response.setTahun(entity.getTahun());
        response.setKuota(entity.getKuota());
        response.setKuotaTerpakai(entity.getKuotaTerpakai());
        response.setKuotaTambahan(entity.getKuotaTambahan());
        response.setSisaKuota(entity.getSisaKuota());
        response.setExpired(entity.getExpired());
        return response;
    }

    public static List<CutiKuotaResponse> fromList(List<CutiKuota> cutiKuota) {
        return cutiKuota.stream().map(CutiKuotaResponse::from).toList();
    }
}
