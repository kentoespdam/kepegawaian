package id.perumdamts.kepegawaian.services.users;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.users.UserProjection;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import id.perumdamts.kepegawaian.dto.users.UserResponse;
import id.perumdamts.kepegawaian.repositories.users.jooq.UserQueryRepository;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceQueryService {
    private final UserQueryRepository queryRepository;
    private final AuthService authService;
    private final Executor taskExecutor;

    public Page<UserResponse> findPage(UserRequest request) {
        Page<UserProjection> projectionPage = queryRepository.findPage(request);
        List<CompletableFuture<UserResponse>> futures = projectionPage.getContent().stream()
                .map(this::fetchUserAsync)
                .toList();
        List<UserResponse> list = futures.stream().map(CompletableFuture::join).toList();
        return new PageImpl<>(list, projectionPage.getPageable(), projectionPage.getTotalElements());
    }

    private CompletableFuture<UserResponse> fetchUserAsync(UserProjection projection) {
        return CompletableFuture.supplyAsync(() -> {
            AppwriteUser appwriteUser = authService.getUser(projection.getId().toString());
            return UserResponse.build(projection, appwriteUser);
        }, taskExecutor);
    }
}
