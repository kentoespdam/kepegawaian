package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiReadMapper;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;

import java.time.LocalDate;
import java.util.List;

public record CutiKuotaResponse(
        Long id,
        PegawaiMiniResponse pegawai,
        Integer tahun,
        Integer kuota,
        Integer kuotaTerpakai,
        Integer kuotaTambahan,
        Integer sisaKuota,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expired
) {
    public static CutiKuotaResponse from(CutiKuota entity) {
        return new CutiKuotaResponse(
                entity.getId(),
                PegawaiReadMapper.toMiniResponse(entity.getPegawai()),
                entity.getTahun(),
                entity.getKuota(),
                entity.getKuotaTerpakai(),
                entity.getKuotaTambahan(),
                entity.getSisaKuota(),
                entity.getExpired()
        );
    }

    public static List<CutiKuotaResponse> fromList(List<CutiKuota> cutiKuota) {
        return cutiKuota.stream().map(CutiKuotaResponse::from).toList();
    }
}
