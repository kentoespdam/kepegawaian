package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GajiBatchMasterPostRequest {
    private MultipartFile file;
}
