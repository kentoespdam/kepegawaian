package id.perumdamts.kepegawaian.entities.cuti;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Table(indexes = {
    @Index(name = "is_deleted_idx", columnList = "is_deleted")
})
@Data
@Audited
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE cuti_kuota SET is_deleted = true where id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor
@AllArgsConstructor
public class CutiKuota extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private Integer tahun;
    private Integer kuota=0;
    private Integer kuotaTerpakai=0;
    private Integer kuotaTambahan=0;
    private Integer sisaKuota=0;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expired;
}
