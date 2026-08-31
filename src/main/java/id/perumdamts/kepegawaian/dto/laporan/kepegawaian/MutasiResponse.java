package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import java.time.LocalDate;

public record MutasiResponse(
        EJenisMutasi jenisMutasi,
        String nipam,
        String nama,
        LocalDate tmtBerlaku,
        String namaOrganisasiLama,
        String namaJabatanLama,
        String namaGolongan,
        String namaOrganisasi,
        String namaJabatan,
        String namaGolonganLama,
        String notes
) {}
