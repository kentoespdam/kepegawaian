package id.perumdamts.kepegawaian.dto.appwrite;

import java.util.List;

public record MeResponse(
        String id,
        String name,
        List<String> roles,
        List<String> permissions
) {
}
