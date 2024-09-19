package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class RiwayatMutasiResponse {
    private Long id;
    private RiwayatSkResponse skMutasi;
    @Enumerated(EnumType.ORDINAL)
    private EJenisMutasi jenisMutasi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlaku;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tglBerakhir;
    private GolonganResponse golongan;
    private OrganisasiMiniResponse organisasi;
    private String namaOrganisasi;
    private JabatanMiniResponse jabatan;
    private String namaJabatan;
    private ProfesiMiniResponse profesi;
    private String namaProfesi;
    private GolonganResponse golonganLama;
    private OrganisasiMiniResponse organisasiLama;
    private String namaOrganisasiLama;
    private JabatanMiniResponse jabatanLama;
    private String namaJabatanLama;
    private ProfesiMiniResponse profesiLama;
    private String namaProfesiLama;
    private String notes;

    public static RiwayatMutasiResponse from(RiwayatMutasi entity) {
        RiwayatMutasiResponse response = new RiwayatMutasiResponse();
        response.setId(entity.getId());
        response.setJenisMutasi(entity.getJenisMutasi());
        response.setSkMutasi(RiwayatSkResponse.from(entity.getRiwayatSk()));
        response.setTmtBerlaku(entity.getTmtBerlaku());
        response.setTglBerakhir(entity.getTglBerakhir());
        if (Objects.nonNull(entity.getGolongan())) {
            response.setGolongan(GolonganResponse.from(entity.getGolongan()));
            response.setGolonganLama(GolonganResponse.from(entity.getGolonganLama()));
        }
        if (entity.getOrganisasi() != null) {
            response.setOrganisasi(OrganisasiMiniResponse.from(entity.getOrganisasi()));
            response.setNamaOrganisasi(entity.getNamaOrganisasi());
        }
        if (entity.getJabatan() != null) {
            response.setJabatan(JabatanMiniResponse.from(entity.getJabatan()));
            response.setNamaJabatan(entity.getNamaJabatan());
        }
        if (entity.getProfesi() != null) {
            response.setProfesi(ProfesiMiniResponse.from(entity.getProfesi()));
            response.setNamaProfesi(entity.getNamaProfesi());
        }
        if (entity.getOrganisasiLama() != null) {
            response.setOrganisasiLama(OrganisasiMiniResponse.from(entity.getOrganisasiLama()));
            response.setNamaOrganisasiLama(entity.getNamaOrganisasiLama());
        }
        if (entity.getJabatanLama() != null) {
            response.setJabatanLama(JabatanMiniResponse.from(entity.getJabatanLama()));
            response.setNamaJabatanLama(entity.getNamaJabatanLama());
        }
        if (entity.getProfesiLama() != null) {
            response.setProfesiLama(ProfesiMiniResponse.from(entity.getProfesiLama()));
            response.setNamaProfesiLama(entity.getNamaProfesiLama());
        }

        response.setNotes(entity.getNotes());
        return response;
    }
}
