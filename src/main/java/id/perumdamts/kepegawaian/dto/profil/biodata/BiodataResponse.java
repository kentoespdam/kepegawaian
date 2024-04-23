package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
public class BiodataResponse {
    private String nik;
    private String nama;
    private String tempatLahir;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    private String alamat;
    private String telp;
    @Enumerated(value = EnumType.ORDINAL)
    private EAgama agama;
    private String ibuKandung;
    private JenjangPendidikanResponse pendidikanTerakhir;
    @Enumerated(value = EnumType.STRING)
    private EGolonganDarah golonganDarah;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    private String fotoProfil;
    private String notes;
    private List<KartuIdentitasMiniResponse> kartuIdentitas;

    public static BiodataResponse from(Biodata entity) {
        JenjangPendidikanResponse pendidikanTerakhir = JenjangPendidikanResponse.from(entity.getPendidikanTerakhir());
        List<KartuIdentitasMiniResponse> kartuIdentitas = Objects.isNull(entity.getKartuIdentitas()) ? null :
                KartuIdentitasMiniResponse.from(entity.getKartuIdentitas());
        BiodataResponse response = new BiodataResponse();
        response.setNik(entity.getNik());
        response.setNama(entity.getNama());
        response.setTempatLahir(entity.getTempatLahir());
        response.setTanggalLahir(entity.getTanggalLahir());
        response.setAlamat(entity.getAlamat());
        response.setTelp(entity.getTelp());
        response.setAgama(entity.getAgama());
        response.setIbuKandung(entity.getIbuKandung());
        response.setPendidikanTerakhir(pendidikanTerakhir);
        response.setGolonganDarah(entity.getGolonganDarah());
        response.setStatusKawin(entity.getStatusKawin());
        response.setFotoProfil(entity.getFotoProfil());
        response.setNotes(entity.getNotes());
        response.setKartuIdentitas(kartuIdentitas);
        return response;
    }

    public static List<BiodataResponse> from(List<Biodata> entities) {
        return entities.stream().map(BiodataResponse::from).toList();
    }

}
