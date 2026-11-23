package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SoResponse(
        Long id,
        Long parentId,
        String kodeJabatan,
        String namaJabatan,
        Integer level,
        String namaPegawai,
        String nipam,
        String namaOrganisasi,
        String fotoProfil,
        Boolean isLowong,
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
