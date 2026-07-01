package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "kode"),
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE jabatan SET is_deleted=true WHERE id=?")
public class Jabatan extends MasterBaseEntity {
    private String kode;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Jabatan parent;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;
    private String nama;

    @OneToMany(mappedBy = "jabatan", fetch = FetchType.LAZY)
    public List<Profesi> profesiList;

    public Jabatan(Long id) {
        super(id);
    }

    public Jabatan(String nama) {
        this.nama = nama;
    }

    public Jabatan(Long id, String kode, Jabatan jabatan, Organisasi organisasi, Level level, String nama) {
        super(id);
        this.kode = kode;
        this.parent = jabatan;
        this.organisasi = organisasi;
        this.level = level;
        this.nama = nama;
    }
}
