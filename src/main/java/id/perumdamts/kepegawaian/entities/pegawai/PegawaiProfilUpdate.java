package id.perumdamts.kepegawaian.entities.pegawai;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

/**
 * DTO for {@link Pegawai}
 */
public record PegawaiProfilUpdate(Long id, @NotEmpty String nipam, String biodataNama,
                                  String jabatanNama) implements Serializable {
}