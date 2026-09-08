package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTambahan;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;

public record GajiBatchPotonganTambahanItem(
        String sheetName,
        String batchMasterId,
        String nipam,
        String kode,
        String nama,
        Integer urut,
        EJenisGaji lokalJenisGaji,
        Double nilai
) {
    public GajiBatchPotonganTambahanItem(String sheetName, String batchMasterId, String nipam, String kode, String nama, Double nilai) {
        this(sheetName, batchMasterId, nipam, kode, nama, 99, EJenisGaji.POTONGAN, nilai);
    }
}