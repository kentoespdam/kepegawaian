package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiDto;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiKomponenItemDto;

import java.util.ArrayList;
import java.util.List;

public final class SlipGajiTableHelper {
    private SlipGajiTableHelper() {}

    public static boolean isAdd(SlipGajiKomponenItemDto item) {
        if (item == null) {
            return false;
        }
        String kode = item.kode() != null ? item.kode().trim().toUpperCase() : "";
        if (kode.startsWith("ADD_") || kode.startsWith("ADD") || kode.startsWith("ADHOC")) {
            return true;
        }
        String nama = item.nama() != null ? item.nama().trim().toUpperCase() : "";
        return nama.startsWith("ADD_") || nama.startsWith("ADD ") || nama.startsWith("ADHOC");
    }

    public static List<SlipGajiKomponenItemDto> filterRegularPotongan(SlipGajiDto dto) {
        if (dto == null || dto.potongan() == null) {
            return List.of();
        }
        return dto.potongan().stream()
                .filter(item -> !isAdd(item))
                .toList();
    }

    public static List<SlipGajiKomponenItemDto> filterAdditionalPotongan(SlipGajiDto dto) {
        if (dto == null) {
            return List.of();
        }
        List<SlipGajiKomponenItemDto> result = new ArrayList<>();
        if (dto.potonganTambahan() != null) {
            result.addAll(dto.potonganTambahan());
        }
        if (dto.potongan() != null) {
            for (SlipGajiKomponenItemDto item : dto.potongan()) {
                if (isAdd(item) && !result.contains(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public static List<SlipGajiKomponenItemDto> filterAdditionalPenerimaan(SlipGajiDto dto) {
        if (dto == null) {
            return List.of();
        }
        List<SlipGajiKomponenItemDto> result = new ArrayList<>();
        if (dto.penerimaanTambahan() != null) {
            result.addAll(dto.penerimaanTambahan());
        }
        if (dto.penerimaan() != null) {
            for (SlipGajiKomponenItemDto item : dto.penerimaan()) {
                if (isAdd(item) && !result.contains(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public static void addEmpRow(PdfPTable t, String l1, String v1, String l2, String v2, Font fBold, Font fNormal) {
        PdfPCell c1 = new PdfPCell(new Phrase(l1, fBold)); c1.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c2 = new PdfPCell(new Phrase(": " + (v1 != null ? v1 : "-"), fNormal)); c2.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c3 = new PdfPCell(new Phrase(l2, fBold)); c3.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c4 = new PdfPCell(new Phrase(l2.isEmpty() ? "" : ": " + (v2 != null ? v2 : "-"), fNormal)); c4.setBorder(PdfPCell.NO_BORDER);
        t.addCell(c1); t.addCell(c2); t.addCell(c3); t.addCell(c4);
    }

    public static void addNumberedItemRow(PdfPTable t, String label, String value, Font fNormal) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fNormal)); c1.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c2 = new PdfPCell(new Phrase(":", fNormal)); c2.setBorder(PdfPCell.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell c3 = new PdfPCell(new Phrase(value, fNormal)); c3.setBorder(PdfPCell.NO_BORDER);
        c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c1); t.addCell(c2); t.addCell(c3);
    }

    public static void addTotalLine(PdfPTable t, String label, String value, Font fBold) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fBold));
        c1.setColspan(2); c1.setBorder(PdfPCell.TOP); c1.setPaddingTop(4f);
        PdfPCell c2 = new PdfPCell(new Phrase(value, fBold));
        c2.setBorder(PdfPCell.TOP); c2.setHorizontalAlignment(Element.ALIGN_RIGHT); c2.setPaddingTop(4f);
        t.addCell(c1); t.addCell(c2);
    }

    public static void addSummaryLine(PdfPTable t, String label, String value, Font fNormal) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fNormal));
        c1.setColspan(2); c1.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c2 = new PdfPCell(new Phrase(value, fNormal));
        c2.setBorder(PdfPCell.NO_BORDER); c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c1); t.addCell(c2);
    }

    public static void addSubTotalLine(PdfPTable t, String label, String value, Font fBold) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fBold));
        c1.setColspan(2); c1.setBorder(PdfPCell.TOP); c1.setPaddingTop(3f);
        PdfPCell c2 = new PdfPCell(new Phrase(value, fBold));
        c2.setBorder(PdfPCell.TOP); c2.setHorizontalAlignment(Element.ALIGN_RIGHT); c2.setPaddingTop(3f);
        t.addCell(c1); t.addCell(c2);
    }
}
