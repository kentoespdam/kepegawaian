package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatSkResponse {
    private Long id;
//    private PegawaiResponse pegawai;
    private String nomorSk;
    @Enumerated(EnumType.ORDINAL)
    private EJenisSk jenisSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlaku;
    private GolonganResponse golongan;
    private Double gajiPokok;
    private Integer mkgTahun;
    private Integer mkgBulan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate kenaikanBerikutnya;
    private Integer mkgbTahun;
    private Integer mkgbBulan;
    private Boolean updateMaster;

    public static RiwayatSkResponse from(RiwayatSk entity) {
        RiwayatSkResponse response = new RiwayatSkResponse();
        response.setId(entity.getId());
//        response.setPegawai(PegawaiResponse.from(entity.getPegawai()));
        response.setNomorSk(entity.getNomorSk());
        response.setJenisSk(entity.getJenisSk());
        response.setTmtBerlaku(entity.getTmtBerlaku());
        response.setGolongan(GolonganResponse.from(entity.getGolongan()));
        response.setGajiPokok(entity.getGajiPokok());
        response.setMkgTahun(entity.getMkgTahun());
        response.setMkgBulan(entity.getMkgBulan());
        response.setKenaikanBerikutnya(entity.getKenaikanBerikutnya());
        response.setMkgbTahun(entity.getMkgbTahun());
        response.setMkgbBulan(entity.getMkgbBulan());
        response.setUpdateMaster(entity.getUpdateMaster());
        return response;
    }
}
