package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;

public record PegawaiListResponse(
        Long id,
        String nipam,
        String nama,
        EStatusPegawai statusPegawai,
        OrganisasiMiniResponse organisasi,
        JabatanMiniResponse jabatan,
        GolonganResponse golongan
) {}
