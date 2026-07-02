package id.perumdamts.kepegawaian.dto.system.roles;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrefRoleRequest extends PagedRequest {
    private String id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Specification<PrefRole> getSpecification() {
        return SpecificationBuilder.<PrefRole>of()
                .addLike(id, "id")
                .build();
    }
}
