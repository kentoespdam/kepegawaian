package id.perumdamts.kepegawaian.services.users;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import id.perumdamts.kepegawaian.dto.users.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserResponse> findPage(UserRequest request);
    SavedStatus<?> patchStatus(Long id, UserPatchStatusRequest request);
}
