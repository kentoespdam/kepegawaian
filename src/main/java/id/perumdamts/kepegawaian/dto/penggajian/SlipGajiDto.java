package id.perumdamts.kepegawaian.dto.penggajian;

import java.io.Serializable;
import java.util.List;

public record SlipGajiDto(
        Long batchMasterId,
        String batchRootId,
        String periode,
        Long pegawaiId,
        String nipam,
        String nama,
        String namaJabatan,
        String golongan,
        String pangkat,
        String bank,
        List<SlipGajiKomponenItemDto> penerimaan,
        List<SlipGajiKomponenItemDto> potongan,
        Double totalPenerimaan,
        Double totalPotongan,
        Double selisihPenerimaanPotongan,
        Double pembulatan,
        Double subTotal,
        List<SlipGajiKomponenItemDto> penerimaanTambahan,
        List<SlipGajiKomponenItemDto> potonganTambahan,
        Double totalPenerimaanTambahan,
        Double totalPotonganTambahan,
        Double totalDibayarkan
) implements Serializable {}
