package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "jenis_sp", indexes = {
        @Index(columnList = "kode", unique = true),
        @Index(columnList = "is_deleted")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE jenis_sp SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class JenisSp extends IdsAbstract {
    @Column(name = "kode", nullable = false, columnDefinition = "VARCHAR(10)")
    private String kode;
    private String nama;
    @NotAudited
    @OneToMany(mappedBy = "jenisSp")
    private Set<Sanksi> sanksiSp = new HashSet<>();

    public JenisSp(Long id) {
        super(id);
    }

    public JenisSp(Long id, String kode, String nama) {
        super(id);
        this.kode = kode;
        this.nama = nama;
    }
}
