package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "jenis_sp", indexes = {
        @Index(columnList = "kode", unique = true),
        @Index(columnList = "is_deleted")
})
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE jenis_sp SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class JenisSp extends IdsAbstract {
    @Column(name = "kode", nullable = false, columnDefinition = "VARCHAR(10)")
    private String kode;
    private String nama;
    @NotAudited
    @OneToMany(mappedBy = "jenisSp")
    private List<Sanksi> sanksiSp = new ArrayList<>();

    public JenisSp(Long id) {
        super(id);
    }

    public JenisSp(String kode, String nama, Set<Sanksi> sanksiList) {
        this.kode = kode;
        this.nama = nama;
        this.sanksiSp = sanksiList.stream().toList();
    }

    public JenisSp(String kode, String nama) {
        this.kode = kode;
        this.nama = nama;
    }
}
