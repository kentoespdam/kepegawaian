package id.perumdamts.kepegawaian.dto.penggajian;

import java.io.Serializable;

public record SlipGajiKomponenItemDto(
        String kode,
        String nama,
        Double nilai
) implements Serializable {}
