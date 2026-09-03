package id.perumdamts.kepegawaian.dto.master.organisasi;

public record OrganisasiQuery(Long id, String kode, Integer levelOrganisasi, String nama, String shortName, String category, String group, OrganisasiMiniResponse parent) {}
