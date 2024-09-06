package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatMutasiResponse {
    private Long id;
    private RiwayatSkResponse skMutasi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlaku;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tglBerakhir;
    private OrganisasiMiniResponse organisasi;
    private String namaOrganisasi;
    private JabatanMiniResponse jabatan;
    private String namaJabatan;
    private OrganisasiMiniResponse organisasiLama;
    private String namaOrganisasiLama;
    private JabatanMiniResponse jabatanLama;
    private String namaJabatanLama;
    private String notes;

    public static RiwayatMutasiResponse from(RiwayatMutasi entity) {
        RiwayatMutasiResponse response = new RiwayatMutasiResponse();
        response.setId(entity.getId());
        response.setSkMutasi(RiwayatSkResponse.from(entity.getRiwayatSk()));
        response.setTmtBerlaku(entity.getTmtBerlaku());
        response.setTglBerakhir(entity.getTglBerakhir());
        response.setOrganisasi(OrganisasiMiniResponse.from(entity.getOrganisasi()));
        response.setNamaOrganisasi(entity.getNamaOrganisasi());
        response.setJabatan(JabatanMiniResponse.from(entity.getJabatan()));
        response.setNamaJabatan(entity.getNamaJabatan());
        if (entity.getOrganisasiLama() != null)
            response.setOrganisasiLama(OrganisasiMiniResponse.from(entity.getOrganisasiLama()));
        response.setNamaOrganisasiLama(entity.getNamaOrganisasiLama());
        if (entity.getJabatanLama() != null) {
            response.setJabatanLama(JabatanMiniResponse.from(entity.getJabatanLama()));
        }
        response.setNamaJabatanLama(entity.getNamaJabatanLama());
        response.setNotes(entity.getNotes());
        return response;
    }
}
