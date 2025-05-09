package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkResponse;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RiwayatTerminasiResponse {
    private Long id;
    private AlasanBerhentiResponse alasanTerminasi;
    private PegawaiResponse pegawai;
    private String nipam;
    private String nama;
    private String nomorSk;
    private RiwayatSkResponse skTerminasi;
    private LampiranSkResponse lampiranSkTerminasi;
    private OrganisasiMiniResponse organisasi;
    private String namaOrganisasi;
    private JabatanMiniResponse jabatan;
    private String namaJabatan;
    private GolonganResponse golongan;
    private String namaGolongan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerminasi;
    private Integer tahunTerminasi;
    private Integer masaKerja;
    private String notes;

    public static RiwayatTerminasiResponse from(RiwayatTerminasi entity) {
        RiwayatTerminasiResponse response = new RiwayatTerminasiResponse();
        response.setId(entity.getId());
        response.setAlasanTerminasi(AlasanBerhentiResponse.from(entity.getAlasanTerminasi()));
        response.setPegawai(PegawaiResponse.from(entity.getPegawai()));
        response.setNipam(entity.getNipam());
        response.setNama(entity.getNama());
        response.setNomorSk(entity.getNomorSk());
        response.setSkTerminasi(RiwayatSkResponse.from(entity.getSkTerminasi()));
        response.setOrganisasi(OrganisasiMiniResponse.from(entity.getOrganisasi()));
        response.setNamaOrganisasi(entity.getNamaOrganisasi());
        response.setJabatan(JabatanMiniResponse.from(entity.getJabatan()));
        response.setNamaJabatan(entity.getNamaJabatan());
        if (entity.getGolongan() != null) {
            response.setGolongan(GolonganResponse.from(entity.getGolongan()));
            response.setNamaGolongan(entity.getNamaGolongan());
        }
        response.setTanggalTerminasi(entity.getTanggalTerminasi());
        response.setTahunTerminasi(entity.getTahunTerminasi());
        response.setMasaKerja(entity.getMasaKerja());
        response.setNotes(entity.getNotes());
        return response;
    }

    public static RiwayatTerminasiResponse from(RiwayatTerminasi entity, List<LampiranSkResponse> lampiranSkResponses) {
        RiwayatTerminasiResponse response = RiwayatTerminasiResponse.from(entity);
        if (lampiranSkResponses != null && !lampiranSkResponses.isEmpty())
            response.setLampiranSkTerminasi(lampiranSkResponses.getFirst());
        return response;
    }
}
