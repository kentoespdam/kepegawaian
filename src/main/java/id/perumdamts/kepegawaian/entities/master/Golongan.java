package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
        @Index(columnList = "golongan"),
        @Index(columnList = "pangkat"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE golongan SET is_deleted = TRUE WHERE id=?")
@SQLRestriction("is_deleted = FALSE")
@Audited
@Slf4j
public class Golongan extends IdsAbstract {
    private String golongan;
    private String pangkat;

    public Golongan(Long id, String golongan, String pangkat) {
        super(id);
        this.golongan = golongan;
        this.pangkat = pangkat;
    }

    public Golongan(Long id) {
        super(id);
    }

    @Override
    public String toString() {
        return "Golongan{" +
                "golongan='" + golongan + '\'' +
                ", pangkat='" + pangkat + '\'' +
                ", isDeleted='" + getIsDeleted() + '\'' +
                "} " + super.toString();
    }
}
