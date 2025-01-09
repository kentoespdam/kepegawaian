package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(indexes = {
        @Index(columnList = "nipam"),
        @Index(columnList = "nama"),
})
@Data
public class GajiBatchRootErrorLogs  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "root_batch_id", referencedColumnName = "batchId")
    private GajiBatchRoot gajiBatchRoot;
    private String nipam;
    private String nama;
    @Column(columnDefinition = "TEXT")
    private String notes;
}
