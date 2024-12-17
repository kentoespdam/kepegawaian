package id.perumdamts.kepegawaian.entities.kepegawaian;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
@Audited
public class LampiranSp extends IdsAbstract {
    private String mimeType;
    private String fileName;
    private String hashedFileName;
}
