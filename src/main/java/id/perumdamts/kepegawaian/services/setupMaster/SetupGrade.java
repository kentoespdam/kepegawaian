package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.repositories.master.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupGrade implements SetupMaster {
    @Autowired
    private GradeRepository repository;

    @Override
    public void insertBatch() {
        List<Grade> list = new ArrayList<>();
        list.add(new Grade(new Level(5L), 1, 3_000_000D));
        list.add(new Grade(new Level(5L), 2, 3_150_000D));
        list.add(new Grade(new Level(5L), 3, 3_300_000D));
        list.add(new Grade(new Level(6L), 1, 1_930_500D));
        list.add(new Grade(new Level(6L), 2, 2_216_500D));
        list.add(new Grade(new Level(6L), 3, 2_250_500D));
        list.add(new Grade(new Level(6L), 4, 3_500_000D));
        list.add(new Grade(new Level(7L), 1, 715_000D));
        list.add(new Grade(new Level(7L), 2, 1_072_500D));
        list.add(new Grade(new Level(7L), 3, 1_430_000D));
        list.add(new Grade(new Level(7L), 4, 1_787_500D));
        repository.saveAll(list);
    }
}
