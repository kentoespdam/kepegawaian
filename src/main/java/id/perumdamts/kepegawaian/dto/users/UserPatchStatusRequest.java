package id.perumdamts.kepegawaian.dto.users;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPatchStatusRequest {
    // ADR-0039: wajib eksplisit — body kosong tidak boleh meng-unblock user (footgun sebelumnya)
    @NotNull(message = "Status wajib diisi (true=blocked, false=aktif)")
    private Boolean status;
}
