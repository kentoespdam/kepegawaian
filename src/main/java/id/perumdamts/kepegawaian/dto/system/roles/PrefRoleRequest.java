package id.perumdamts.kepegawaian.dto.system.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrefRoleRequest extends CommonPageRequest {
    private String id;

    @JsonIgnore
    public Specification<PrefRole> getSpecification() {
        return Specification.where((root, query, cb) ->
                Objects.isNull(id) ? null :
                        cb.like(root.get("id"), "%" + id + "%"));
    }
}
