package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
        @Index(columnList = "seq"),
        @Index(columnList = "kode"),
        @Index(columnList = "nama"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GajiBatchMasterProses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "master_batch_id", referencedColumnName = "id")
    private GajiBatchMaster masterBatch;
    private Integer seq;
    private String kode;
    private String nama;
    private String ctype;
    private Double nilai;
    private String formula;
    private String nilaiFormula;
}
