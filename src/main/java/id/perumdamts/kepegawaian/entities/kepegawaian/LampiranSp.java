package id.perumdamts.kepegawaian.entities.kepegawaian;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.hibernate.envers.Audited;


@MappedSuperclass
@Getter
@Setter
@ToString
@Audited
public class LampiranSp extends IdsAbstract {
    private String mimeType;
    private String fileName;
    private String hashedFileName;
}
