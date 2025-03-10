package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
        @Index(columnList = "urut"),
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
    @ManyToOne
    @JoinColumn(name = "batch_master_id", referencedColumnName = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private GajiBatchMaster gajiBatchMaster;
    @Column(name = "kode")
    private String kode;
    private Integer urut;
    private String nama;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji;
    private Double nilai;
    private String formula;
    private String nilaiFormula;
}
