package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

/**
 * Embedded lampiran projection for multiset() queries.
 * Maps the 3 columns selected for lampiran in entity detail queries.
 */
public record LampiranRow(Long id, String fileName, String mimeType) {
}
