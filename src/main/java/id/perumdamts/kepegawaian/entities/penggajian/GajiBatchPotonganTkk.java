package id.perumdamts.kepegawaian.entities.penggajian;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(columnList = "batch_id"),
        @Index(columnList = "nipam")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GajiBatchPotonganTkk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String batchId;
    private String nipam;
    private Integer potongan;
}
