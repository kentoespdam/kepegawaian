package id.perumdamts.kepegawaian.services.users;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import id.perumdamts.kepegawaian.dto.users.UserResponse;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PegawaiRepository repository;
    private final AuthService authService;
    private final Executor taskExecutor;

    @Override
    public Page<UserResponse> findPage(UserRequest request) {
        Page<Pegawai> pegawaiPage = repository.findAll(request.getSpecification(), request.getPageable());
        List<CompletableFuture<UserResponse>> futures = pegawaiPage.getContent().stream().map(this::fetchUserAsync).toList();
        List<UserResponse> list = futures.stream().map(CompletableFuture::join).toList();
        return new PageImpl<>(list, pegawaiPage.getPageable(), pegawaiPage.getTotalElements());
    }

    @Override
    public SavedStatus<?> patchStatus(Long id, UserPatchStatusRequest request) {
        return SavedStatus.build(ESaveStatus.SUCCESS, authService.updateStatus(id.toString(), request));
    }

    private CompletableFuture<UserResponse> fetchUserAsync(Pegawai pegawai) {
        return CompletableFuture.supplyAsync(() -> {
            AppwriteUser appwriteUser = authService.getUser(pegawai.getId().toString());
            return UserResponse.build(pegawai, appwriteUser);
        }, taskExecutor);
    }
}
