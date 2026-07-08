package id.perumdamts.kepegawaian.dto.master.grade;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;

public record GradeQuery(Long id, Integer grade, Double tukin, LevelResponse level) {}
