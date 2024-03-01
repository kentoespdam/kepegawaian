package id.perumdamts.kepegawaian.dto.master.pangkat;

import id.perumdamts.kepegawaian.entities.master.Pangkat;
import lombok.Data;

import java.util.List;

@Data
public class PangkatPostRequest {
    private String nama;

    public static Pangkat toEntity(PangkatPostRequest request) {
        return new Pangkat(request.getNama());
    }

    public static Pangkat toEntity(PangkatPostRequest request, Long id) {
        return new Pangkat(id, request.getNama());
    }

    public static List<Pangkat> toEntities(List<PangkatPostRequest> requests) {
        return requests.stream().map(PangkatPostRequest::toEntity).toList();
    }
}
