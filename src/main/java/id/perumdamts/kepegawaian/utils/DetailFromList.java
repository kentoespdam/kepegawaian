package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;

import java.util.List;

public class DetailFromList {
    public static Organisasi findExistOrganisasi(List<Organisasi> organisasiList, Long organisasiId) {
        return organisasiList.stream()
                .filter(organisasi -> organisasi.getId().equals(organisasiId))
                .findFirst()
                .orElse(null);
    }

    public static Jabatan findExistJabatan(List<Jabatan> jabatanList, Long jabatanId) {
        return jabatanList.stream()
                .filter(jabatan -> jabatan.getId().equals(jabatanId))
                .findFirst()
                .orElse(null);
    }

    public static Profesi findExistProfesi(List<Profesi> profesiList, Long profesiId) {
        return profesiList.stream()
                .filter(profesi -> profesi.getId().equals(profesiId))
                .findFirst()
                .orElse(null);
    }

    public static Golongan findExistGolongan(List<Golongan> golonganList, Long golonganId) {
        return golonganList.stream()
                .filter(golongan -> golongan.getId().equals(golonganId))
                .findFirst()
                .orElse(null);
    }
}
