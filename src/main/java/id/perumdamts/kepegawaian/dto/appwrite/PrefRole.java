package id.perumdamts.kepegawaian.dto.appwrite;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pref_role")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrefRole {
    @Id
    String id;
}
