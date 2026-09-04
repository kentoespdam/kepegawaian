package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(indexes = {
        @Index(columnList = "nipam"),
        @Index(columnList = "nama"),
})
@Getter
@Setter
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
    private String notes;
    @Enumerated(EnumType.STRING)
    private EJenisErrorGaji jenisError = EJenisErrorGaji.SYSTEM;
}
