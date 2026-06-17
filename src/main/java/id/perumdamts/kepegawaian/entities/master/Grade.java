package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "grade"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE grade SET is_deleted=true WHERE id=?")
public class Grade extends MasterBaseEntity {

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;
    private Integer grade;
    private Double tukin;

    @OneToMany(mappedBy = "grade", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Profesi> profesiList;

    public Grade(Long id) {
        super(id);
    }

    public Grade(Level level, int grade, double tukin) {
        this.level = level;
        this.grade = grade;
        this.tukin = tukin;
    }
}
