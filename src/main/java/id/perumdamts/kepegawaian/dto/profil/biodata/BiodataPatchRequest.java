package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BiodataPatchRequest {
    private String nama;
    private String alamat;
    @Enumerated(EnumType.ORDINAL)
    private EJenisKelamin jenisKelamin;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    @Enumerated(EnumType.ORDINAL)
    private EAgama agama;
    private String tempatLahir;
    private LocalDate tanggalLahir;
    private String ibuKandung;
    private String telp;
}
