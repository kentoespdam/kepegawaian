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
import java.util.Objects;

@Data
public class RiwayatSkResponse {
    private Long id;
    private String nipam;
    private String nama;
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
    private String notes;

    public static RiwayatSkResponse from(RiwayatSk entity) {
        RiwayatSkResponse response = new RiwayatSkResponse();
        response.setId(entity.getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getNama());
        response.setNomorSk(entity.getNomorSk());
        response.setJenisSk(entity.getJenisSk());
        response.setTanggalSk(entity.getTanggalSk());
        response.setTmtBerlaku(entity.getTmtBerlaku());
        if (Objects.nonNull(entity.getGolongan()))
            response.setGolongan(GolonganResponse.from(entity.getGolongan()));
        response.setGajiPokok(entity.getGajiPokok());
        response.setMkgTahun(entity.getMkgTahun());
        response.setMkgBulan(entity.getMkgBulan());
        response.setKenaikanBerikutnya(entity.getKenaikanBerikutnya());
        response.setMkgbTahun(entity.getMkgbTahun());
        response.setMkgbBulan(entity.getMkgbBulan());
        response.setUpdateMaster(entity.getUpdateMaster());
        response.setNotes(entity.getNotes());
        return response;
    }
}
