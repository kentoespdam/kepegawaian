package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GajiBatchRootPostRequest {
    private String tahun;
    private String bulan;
    private String diProsesOleh;
    private String jabatanPemroses;
    private MultipartFile fileName;

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

}
