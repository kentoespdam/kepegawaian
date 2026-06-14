package id.perumdamts.kepegawaian.dto.master.organisasi;

import lombok.Data;

@Data
public class OrganisasiQuery {
    private Long id;
    private String kode;
    private Long parentId;
    private OrganisasiMiniResponse parent;
    private Integer levelOrganisasi;
    private String nama;
    private String shortName;
    private String category;
}
