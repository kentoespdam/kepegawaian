package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.config.appwrite.AppwriteClient;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {
    private final AppwriteClient appwriteClient;

    public AppwriteUser getUserFromToken(String token) {
        return appwriteClient.validateToken(token);
    }
}
