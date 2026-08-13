package id.perumdamts.kepegawaian.dto.system.roles;

import lombok.Data;

@Data
public class PrefRoleUpdateRequest {
    // ADR-0039: nullable — null menghapus/me-reset description
    private String description;
}
