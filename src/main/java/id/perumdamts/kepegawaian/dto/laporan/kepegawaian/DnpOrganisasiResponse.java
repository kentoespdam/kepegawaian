package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.util.List;

public record DnpOrganisasiResponse(
        String kode,
        String nama,
        List<DnpResponse> pegawai
) {}
