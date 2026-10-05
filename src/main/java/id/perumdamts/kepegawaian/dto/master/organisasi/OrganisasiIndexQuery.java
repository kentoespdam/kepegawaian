package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganisasiIndexQuery extends PagedRequest {
    private String kode;
    private String nama;
    private Long parentId;
    private Integer levelOrg;
    private String category;
    private String group;


}
