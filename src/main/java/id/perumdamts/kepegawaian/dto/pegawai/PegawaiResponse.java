package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiResponse;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaResponse;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;

@Data
public class PegawaiResponse {
    private Long id;
    private String nipam;
    private BiodataMiniResponse biodata;
    private StatusPegawaiResponse statusPegawai;
    private JabatanMiniResponse jabatan;
    private OrganisasiResponse organisasi;
    private ProfesiResponse profesi;
    private GolonganResponse golongan;
    private GradeResponse grade;
    private StatusKerjaResponse statusKerja;
    private String notes;

    public static PegawaiResponse from(Pegawai pegawai) {
        PegawaiResponse response = new PegawaiResponse();
        response.setId(pegawai.getId());
        response.setNipam(pegawai.getNipam());
        response.setBiodata(BiodataMiniResponse.from(pegawai.getBiodata()));
        response.setStatusPegawai(StatusPegawaiResponse.from(pegawai.getStatusPegawai()));
        response.setJabatan(JabatanMiniResponse.from(pegawai.getJabatan()));
        response.setOrganisasi(OrganisasiResponse.from(pegawai.getOrganisasi()));
        response.setProfesi(ProfesiResponse.from(pegawai.getProfesi()));
        response.setGolongan(GolonganResponse.from(pegawai.getGolongan()));
        response.setGrade(GradeResponse.from(pegawai.getGrade()));
        response.setStatusKerja(StatusKerjaResponse.from(pegawai.getStatusKerja()));
        response.setNotes(pegawai.getNotes());

        return response;
    }
}
