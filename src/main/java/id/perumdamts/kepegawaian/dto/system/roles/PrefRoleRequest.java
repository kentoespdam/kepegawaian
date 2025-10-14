package id.perumdamts.kepegawaian.dto.system.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrefRoleRequest extends CommonPageRequest {
    private String id;

    @JsonIgnore
    public Specification<PrefRole> getSpecification() {
        return SpecificationBuilder.<PrefRole>of()
                .addLike(id, "id")
                .build();
    }
}
