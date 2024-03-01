package id.perumdamts.kepegawaian.dto.master.golongan;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import lombok.Data;

import java.util.List;

@Data
public class GolonganPostRequest {
    private String nama;

    public static Golongan toEntity(GolonganPostRequest request) {
        return new Golongan(request.getNama());
    }

    public static Golongan toEntity(GolonganPostRequest request, Long id) {
        return new Golongan(id, request.getNama());
    }

    public static List<Golongan> toEntities(List<GolonganPostRequest> requests) {
        return requests.stream().map(GolonganPostRequest::toEntity).toList();
    }
}
