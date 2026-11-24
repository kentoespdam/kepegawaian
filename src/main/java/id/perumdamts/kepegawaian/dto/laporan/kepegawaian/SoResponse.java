package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Data struktur organisasi kepegawaian")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SoResponse(
        @Schema(description = "ID Jabatan", example = "1")
        Long id,
        @Schema(description = "ID Jabatan Atasan (null untuk Dewan Pengawas)", example = "null")
        Long parentId,
        @Schema(description = "Kode Jabatan", example = "0")
        String kodeJabatan,
        @Schema(description = "Nama Jabatan", example = "Kepala Dewan Pengawas")
        String namaJabatan,
        @Schema(description = "Level Jabatan (1: Dewas s.d. 7: Staf)", example = "1")
        Integer level,
        @Schema(description = "Nama Pejabat Aktif (null jika lowong)", example = "Drs. H. Suwandi, M.M.")
        String namaPegawai,
        @Schema(description = "NIPAM Pejabat Aktif (null jika lowong)", example = "8901001")
        String nipam,
        @Schema(description = "Nama Unit Organisasi", example = "DEWAN PENGAWAS")
        String namaOrganisasi,
        @Schema(description = "Path atau URL foto profil", example = "foto.jpg")
        String fotoProfil,
        @Schema(description = "Status apakah jabatan belum terisi pejabat aktif", example = "false")
        Boolean isLowong,
        @Schema(description = "Daftar bawahan langsung (hanya muncul saat format=tree)")
        List<SoResponse> children
) {
    public static SoResponse flat(
            Long id,
            Long parentId,
            String kodeJabatan,
            String namaJabatan,
            Integer level,
            String namaPegawai,
            String nipam,
            String namaOrganisasi,
            String fotoProfil,
            Boolean isLowong
    ) {
        return new SoResponse(
                id,
                parentId,
                kodeJabatan,
                namaJabatan,
                level,
                namaPegawai,
                nipam,
                namaOrganisasi,
                fotoProfil,
                isLowong,
                null
        );
    }

    public SoResponse withChildren(List<SoResponse> children) {
        return new SoResponse(
                this.id,
                this.parentId,
                this.kodeJabatan,
                this.namaJabatan,
                this.level,
                this.namaPegawai,
                this.nipam,
                this.namaOrganisasi,
                this.fotoProfil,
                this.isLowong,
                children
        );
    }
}
