package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchPotonganTkk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GajiBatchPotonganTkkRepository extends JpaRepository<GajiBatchPotonganTkk, Long> {
    @Query("select coalesce(sum(p.potongan), 0) from GajiBatchPotonganTkk p where p.batchId = ?1 and p.nipam = ?2")
    Long sumPotonganByBatchIdAndNipam(String batchId, String nipam);

    @Query("select p.nipam, coalesce(sum(p.potongan), 0) from GajiBatchPotonganTkk p where p.batchId = ?1 group by p.nipam")
    List<Object[]> sumPotonganGroupByNipam(String batchId);
}
