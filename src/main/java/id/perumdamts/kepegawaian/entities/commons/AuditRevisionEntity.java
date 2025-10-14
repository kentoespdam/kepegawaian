package id.perumdamts.kepegawaian.entities.commons;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

//@Entity
//@RevisionEntity(AuditRevisionListener.class)
//@Getter
//@Setter
public class AuditRevisionEntity {
    @Id
    @GeneratedValue
    @RevisionNumber
    private long id;

    @RevisionTimestamp
    private long timestamp;

    private String username;
    private String action;
}
