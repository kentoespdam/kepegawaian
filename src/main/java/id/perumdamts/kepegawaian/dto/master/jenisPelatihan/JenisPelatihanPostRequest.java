package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JenisPelatihanPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;



}
