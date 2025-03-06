package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GajiBatchRootErrorLogsRepository extends JpaRepository<GajiBatchRootErrorLogs, Long> {
    List<GajiBatchRootErrorLogs> findByGajiBatchRoot_Id(String id);
}
