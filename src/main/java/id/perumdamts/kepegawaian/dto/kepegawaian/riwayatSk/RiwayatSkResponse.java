package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record RiwayatSkResponse(
        Long id,
        String nipam,
        String nama,
        String nomorSk,
        EJenisSk jenisSk,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSk,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtBerlaku,
        GolonganResponse golongan,
        Double gajiPokok,
        Integer mkgTahun,
        Integer mkgBulan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate kenaikanBerikutnya,
        Integer mkgbTahun,
        Integer mkgbBulan,
        Boolean updateMaster,
        String notes
) {
    public static RiwayatSkResponse from(RiwayatSk entity) {
        return new RiwayatSkResponse(
                entity.getId(),
                entity.getNipam(),
                entity.getNama(),
                entity.getNomorSk(),
                entity.getJenisSk(),
                entity.getTanggalSk(),
                entity.getTmtBerlaku(),
                Objects.nonNull(entity.getGolongan()) ? GolonganResponse.from(entity.getGolongan()) : null,
                entity.getGajiPokok(),
                entity.getMkgTahun(),
                entity.getMkgBulan(),
                entity.getKenaikanBerikutnya(),
                entity.getMkgbTahun(),
                entity.getMkgbBulan(),
                entity.getUpdateMaster(),
                entity.getNotes()
        );
    }

    public static RiwayatSkResponse getLastFromList(List<RiwayatSk> list, EJenisSk jenisSk){
        Optional<RiwayatSk> first = list.stream().filter(riwayatSk -> riwayatSk.getJenisSk().equals(jenisSk)).findFirst();
        return first.map(RiwayatSkResponse::from).orElse(null);
    }
}
