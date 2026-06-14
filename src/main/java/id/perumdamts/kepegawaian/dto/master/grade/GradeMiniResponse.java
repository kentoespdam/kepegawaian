package id.perumdamts.kepegawaian.dto.master.grade;

import id.perumdamts.kepegawaian.entities.master.Grade;
import lombok.Data;

@Data
public class GradeMiniResponse {
    private Long id;
    private Integer grade;
    private Double tukin;

    public static GradeMiniResponse from(Grade entity) {
        if (entity == null) return null;
        GradeMiniResponse response = new GradeMiniResponse();
        response.setId(entity.getId());
        response.setGrade(entity.getGrade());
        response.setTukin(entity.getTukin());
        return response;
    }
}
