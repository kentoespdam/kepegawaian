package id.perumdamts.kepegawaian.dto.master.grade;

import id.perumdamts.kepegawaian.entities.master.Grade;

public record GradeMiniResponse(Long id, Integer grade, Double tukin) {
    public static GradeMiniResponse from(Grade entity) {
        if (entity == null) return null;
        return new GradeMiniResponse(entity.getId(), entity.getGrade(), entity.getTukin());
    }
}
