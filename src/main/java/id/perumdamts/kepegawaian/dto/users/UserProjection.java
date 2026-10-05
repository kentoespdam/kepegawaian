package id.perumdamts.kepegawaian.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProjection {
    private Long id;
    private String nipam;
    private String nama;
}
