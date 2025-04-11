package id.perumdamts.kepegawaian.dto.appwrite;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppwriteUserPostRequest {
    private String userId;
    private String email;
    private String phone;
    private String password;
    private String name;
}
