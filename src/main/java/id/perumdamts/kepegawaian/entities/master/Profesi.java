package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE profesi SET is_deleted=true WHERE id=?")
public class Profesi extends MasterBaseEntity {
    private String nama;
    private String detail;
    private String resiko;
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;
    @ManyToOne
    private Grade grade;

    @OneToMany(mappedBy = "profesi")
    private List<AlatKerja> alatKerjaList;
    @OneToMany(mappedBy = "profesi")
    private List<Apd> apdList;

    public Profesi(Long id) {
        super(id);
    }

    public Profesi(String nama, String detail, String resiko, Organisasi organisasi, Jabatan jabatan, Level level, Grade grade) {
        this.nama = nama;
        this.detail = detail;
        this.resiko = resiko;
        this.organisasi = organisasi;
        this.jabatan = jabatan;
        this.level = level;
        this.grade = grade;
    }
}
