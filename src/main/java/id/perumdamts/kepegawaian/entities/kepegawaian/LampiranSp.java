package id.perumdamts.kepegawaian.entities.kepegawaian;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@MappedSuperclass
@Getter
@Setter
@Audited
public class LampiranSp extends IdsAbstract {
    private String mimeType;
    private String fileName;
    private String hashedFileName;
}
