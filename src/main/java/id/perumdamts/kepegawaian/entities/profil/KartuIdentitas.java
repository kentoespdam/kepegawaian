package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(columnList = "nomor_kartu"),
        @Index(columnList = "is_deleted")
},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"nik", "jenis_kitas_id"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE kartu_identitas SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class KartuIdentitas extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "nik", referencedColumnName = "nik")
    private Biodata biodata;
    @ManyToOne
    @JoinColumn(name = "jenis_kitas_id", referencedColumnName = "id")
    private JenisKitas jenisKartu;
    private String nomorKartu;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalExpired;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerima;
    private String notes;
    @Column(columnDefinition = "boolean default false")
    private Boolean changedStatus;

    public KartuIdentitas(Biodata biodata) {
        JenisKitas jenisKitas = new JenisKitas(1L);
        this.biodata = biodata;
        this.jenisKartu = jenisKitas;
        this.nomorKartu = biodata.getNik();
    }
}
