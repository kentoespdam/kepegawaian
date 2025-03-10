package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class GajiBatchRootPostRequest {
    private String tahun;
    private String bulan;
    private String diProsesOleh;
    private String jabatanPemroses;
    private MultipartFile file;

    @JsonIgnore
    public String getPeriode() {
        return this.tahun + this.bulan;
    }

    @JsonIgnore
    public String getBatchId() {
        return this.getPeriode() + "-" + "001";
    }

    @JsonIgnore
    public String nextBatchId(String oldBatchId) {
        String[] arrString = oldBatchId.split("-");
        int urut = Integer.parseInt(arrString[1]) + 1;
        String urutString = urut < 10 ? "00" + urut : String.valueOf(urut);
        return arrString[0] + "-" + urutString;
    }

    public static GajiBatchRoot toEntityPhase1(GajiBatchRootPostRequest request) {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId(request.getBatchId());
        entity.setPeriode(request.getPeriode());
        entity.setStatus(EProsesGaji.PENDING.value());
        entity.setDiProsesOleh(request.getDiProsesOleh());
        entity.setJabatanPemroses(request.getJabatanPemroses());
        return entity;
    }

    public static GajiBatchRoot toEntityPhase2(GajiBatchRoot entity) {
        entity.setTanggalProses(LocalDateTime.now());
        entity.setStatus(EProsesGaji.PROSES.value());
        return entity;
    }
}
