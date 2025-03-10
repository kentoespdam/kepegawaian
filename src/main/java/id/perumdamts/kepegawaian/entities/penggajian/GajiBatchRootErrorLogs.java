package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(indexes = {
        @Index(columnList = "nipam"),
        @Index(columnList = "nama"),
})
@Data
public class GajiBatchRootErrorLogs implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "root_batch_id", referencedColumnName = "id")
    @JsonBackReference
    @JsonIdentityReference(alwaysAsId = true)
    private GajiBatchRoot gajiBatchRoot;
    private String nipam;
    private String nama;
    @Column(columnDefinition = "TEXT")
    private String notes;
}
