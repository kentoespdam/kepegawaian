package id.perumdamts.kepegawaian.dto.system.roles;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrefRoleStoreRequest {
    @NotBlank(message = "Role ID wajib diisi")
    private String id;

    // ADR-0039: label opsional untuk UI manajemen role
    private String description;
}
