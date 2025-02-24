package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gaji_batch_root_lampiran")
@EqualsAndHashCode(callSuper = true)
@Data
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE gaji_batch_root_lampiran SET deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class GajiBatchRootLampiran extends IdsAbstract {
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "root_batch_id", referencedColumnName = "batchId")
    private GajiBatchRoot gajiBatchRoot;
    @Enumerated(EnumType.ORDINAL)
    private EJenisPotonganGaji jenisLampiranGaji;
    private String mimeType;
    private String fileName;
    private String hashedFileName;
}
