package id.perumdamts.kepegawaian.services.users;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import id.perumdamts.kepegawaian.dto.users.UserResponse;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceQueryService queryService;
    private final AuthService authService;

    public Page<UserResponse> findPage(UserRequest request) {
        return queryService.findPage(request);
    }

    // ADR-0039: id Appwrite user adalah String (sama dengan pegawai.id), seragam dengan endpoint pref/{id}
    public SavedStatus<AppwriteUser> patchStatus(String id, UserPatchStatusRequest request) {
        return SavedStatus.build(ESaveStatus.SUCCESS, authService.updateStatus(id, request));
    }
}
