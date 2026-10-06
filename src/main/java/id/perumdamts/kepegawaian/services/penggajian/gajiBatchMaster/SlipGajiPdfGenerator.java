package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import id.perumdamts.kepegawaian.config.SlipGajiProperties;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiDto;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiKomponenItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlipGajiPdfGenerator {
    private final SlipGajiProperties properties;
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);
    private static final Font FONT_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);
    private static final Font FONT_SMALL = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);

    private String formatRupiah(Double amount) {
        if (amount == null) amount = 0.0;
        return "Rp. " + new DecimalFormat("#,##0", new DecimalFormatSymbols(Locale.US)).format(amount);
    }
    private String formatPeriode(String p) {
        if (p == null || p.isBlank()) return "-";
        try { return YearMonth.parse(p).format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.of("id", "ID"))); } catch (Exception e) { return p; }
    }
    public byte[] generatePdf(SlipGajiDto dto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document);
            addTitle(document, dto);
            addEmployeeData(document, dto);
            addMainComponents(document, dto);
            addAdditionalComponents(document, dto);
            addFooter(document);
            document.close();
        } catch (DocumentException e) {
            log.error("Gagal generate PDF slip gaji: {}", e.getMessage(), e);
            throw new IllegalStateException("Gagal generate PDF slip gaji", e);
        }
        return baos.toByteArray();
    }
    private void addHeader(Document document) throws DocumentException {
        PdfPTable kopTable = new PdfPTable(2);
        kopTable.setWidthPercentage(100);
        kopTable.setWidths(new float[]{15f, 85f});
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(PdfPCell.BOTTOM); logoCell.setBorderWidthBottom(1.5f); logoCell.setPaddingBottom(8f);
        addLogo(logoCell);
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(PdfPCell.BOTTOM); infoCell.setBorderWidthBottom(1.5f); infoCell.setPaddingBottom(8f);
        infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        addHeaderParagraph(infoCell, properties.kop().instansi(), FONT_TITLE);
        addHeaderParagraph(infoCell, properties.kop().unitKerja(), FONT_SUBTITLE);
        addHeaderParagraph(infoCell, properties.kop().alamat(), FONT_SMALL);
        addHeaderParagraph(infoCell, "Telp. " + properties.kop().telepon() + " Fax. " + properties.kop().fax(), FONT_SMALL);
        addHeaderParagraph(infoCell, "website: " + properties.kop().website() + " E-mail: " + properties.kop().email(), FONT_SMALL);
        kopTable.addCell(logoCell); kopTable.addCell(infoCell);
        document.add(kopTable);
    }
    private void addLogo(PdfPCell logoCell) {
        try {
            ClassPathResource resource = new ClassPathResource(properties.kop().logoPath());
            if (resource.exists()) {
                Image img = Image.getInstance(resource.getURL());
                img.scaleAbsolute(45, 45);
                logoCell.addElement(img);
            }
        } catch (Exception e) { log.warn("Logo slip gaji tidak ditemukan: {}", e.getMessage()); }
    }
    private void addHeaderParagraph(PdfPCell cell, String text, Font font) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
    }
    private void addTitle(Document document, SlipGajiDto dto) throws DocumentException {
        Paragraph title = new Paragraph("SLIP GAJI PEGAWAI", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER); title.setSpacingBefore(10f);
        document.add(title);
        Paragraph periode = new Paragraph(formatPeriode(dto.periode()), FONT_SUBTITLE);
        periode.setAlignment(Element.ALIGN_CENTER); periode.setSpacingAfter(10f);
        document.add(periode);
    }
    private void addEmployeeData(Document document, SlipGajiDto dto) throws DocumentException {
        PdfPTable empTable = new PdfPTable(4);
        empTable.setWidthPercentage(100); empTable.setWidths(new float[]{12f, 48f, 15f, 25f});
        String golPangkat = (dto.golongan() != null ? dto.golongan() : "") + (dto.pangkat() != null ? " - " + dto.pangkat() : "");
        SlipGajiTableHelper.addEmpRow(empTable, "Nama", dto.nama(), "Golongan", golPangkat, FONT_BOLD, FONT_NORMAL);
        SlipGajiTableHelper.addEmpRow(empTable, "NIPAM", dto.nipam(), "Bank", dto.bank(), FONT_BOLD, FONT_NORMAL);
        SlipGajiTableHelper.addEmpRow(empTable, "Jabatan", dto.namaJabatan(), "", "", FONT_BOLD, FONT_NORMAL);
        empTable.setSpacingAfter(8f);
        document.add(empTable);
    }
    private void addMainComponents(Document document, SlipGajiDto dto) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100); mainTable.setWidths(new float[]{50f, 50f});
        PdfPCell leftCell = new PdfPCell(); leftCell.setPadding(6f);
        PdfPTable leftTable = new PdfPTable(3);
        leftTable.setWidthPercentage(100); leftTable.setWidths(new float[]{65f, 5f, 30f});
        PdfPCell hLeft = new PdfPCell(new Phrase("Penerimaan ( + )", FONT_BOLD));
        hLeft.setColspan(3); hLeft.setBorder(PdfPCell.NO_BORDER); hLeft.setPaddingBottom(6f);
        leftTable.addCell(hLeft);
        if (dto.penerimaan() != null) {
            int num = 1;
            for (SlipGajiKomponenItemDto item : dto.penerimaan()) {
                if (SlipGajiTableHelper.isAdd(item)) continue;
                SlipGajiTableHelper.addNumberedItemRow(leftTable, num++ + ". " + item.nama(), formatRupiah(item.nilai()), FONT_NORMAL);
            }
        }
        SlipGajiTableHelper.addTotalLine(leftTable, "Total Penerimaan", formatRupiah(dto.totalPenerimaan()), FONT_BOLD);
        leftCell.addElement(leftTable);
        PdfPCell rightCell = new PdfPCell(); rightCell.setPadding(6f);
        PdfPTable rightTable = new PdfPTable(3);
        rightTable.setWidthPercentage(100); rightTable.setWidths(new float[]{65f, 5f, 30f});
        PdfPCell hRight = new PdfPCell(new Phrase("Potongan ( - )", FONT_BOLD));
        hRight.setColspan(3); hRight.setBorder(PdfPCell.NO_BORDER); hRight.setPaddingBottom(6f);
        rightTable.addCell(hRight);
        List<SlipGajiKomponenItemDto> regularPotongan = SlipGajiTableHelper.filterRegularPotongan(dto);
        if (regularPotongan != null) {
            int num = 1;
            for (SlipGajiKomponenItemDto item : regularPotongan) {
                SlipGajiTableHelper.addNumberedItemRow(rightTable, num++ + ". " + item.nama(), formatRupiah(item.nilai()), FONT_NORMAL);
            }
        }
        SlipGajiTableHelper.addTotalLine(rightTable, "Total Potongan", formatRupiah(dto.totalPotongan()), FONT_BOLD);
        SlipGajiTableHelper.addSummaryLine(rightTable, "Penerimaan - Potongan", formatRupiah(dto.selisihPenerimaanPotongan()), FONT_NORMAL);
        SlipGajiTableHelper.addSummaryLine(rightTable, "Pembulatan", formatRupiah(dto.pembulatan()), FONT_NORMAL);
        SlipGajiTableHelper.addSubTotalLine(rightTable, "Sub Total", formatRupiah(dto.subTotal()), FONT_BOLD);
        rightCell.addElement(rightTable);
        mainTable.addCell(leftCell); mainTable.addCell(rightCell); mainTable.setSpacingAfter(8f);
        document.add(mainTable);
    }
    private void addAdditionalComponents(Document document, SlipGajiDto dto) throws DocumentException {
        PdfPTable addTable = new PdfPTable(2);
        addTable.setWidthPercentage(100); addTable.setWidths(new float[]{50f, 50f});
        PdfPCell leftCell = new PdfPCell(); leftCell.setPadding(6f);
        PdfPTable leftTable = new PdfPTable(3);
        leftTable.setWidthPercentage(100); leftTable.setWidths(new float[]{65f, 5f, 30f});
        PdfPCell hLeft = new PdfPCell(new Phrase("Penerimaan Tambahan", FONT_BOLD));
        hLeft.setColspan(3); hLeft.setBorder(PdfPCell.NO_BORDER); hLeft.setPaddingBottom(6f);
        leftTable.addCell(hLeft);
        List<SlipGajiKomponenItemDto> addPenerimaan = SlipGajiTableHelper.filterAdditionalPenerimaan(dto);
        if (addPenerimaan != null && !addPenerimaan.isEmpty()) {
            int num = 1;
            for (SlipGajiKomponenItemDto item : addPenerimaan) {
                SlipGajiTableHelper.addNumberedItemRow(leftTable, num++ + ". " + item.nama(), formatRupiah(item.nilai()), FONT_NORMAL);
            }
        } else {
            PdfPCell empty = new PdfPCell(new Phrase("(tidak ada)", FONT_NORMAL));
            empty.setColspan(3); empty.setBorder(PdfPCell.NO_BORDER); empty.setPaddingBottom(4f);
            leftTable.addCell(empty);
        }
        SlipGajiTableHelper.addTotalLine(leftTable, "Total Penerimaan Tambahan", formatRupiah(dto.totalPenerimaanTambahan()), FONT_BOLD);
        leftCell.addElement(leftTable);
        PdfPCell rightCell = new PdfPCell(); rightCell.setPadding(6f);
        PdfPTable rightTable = new PdfPTable(3);
        rightTable.setWidthPercentage(100); rightTable.setWidths(new float[]{65f, 5f, 30f});
        PdfPCell hRight = new PdfPCell(new Phrase("Potongan Tambahan", FONT_BOLD));
        hRight.setColspan(3); hRight.setBorder(PdfPCell.NO_BORDER); hRight.setPaddingBottom(6f);
        rightTable.addCell(hRight);
        List<SlipGajiKomponenItemDto> addPotongan = SlipGajiTableHelper.filterAdditionalPotongan(dto);
        if (addPotongan != null && !addPotongan.isEmpty()) {
            int num = 1;
            for (SlipGajiKomponenItemDto item : addPotongan) {
                SlipGajiTableHelper.addNumberedItemRow(rightTable, num++ + ". " + item.nama(), formatRupiah(item.nilai()), FONT_NORMAL);
            }
        } else {
            PdfPCell empty = new PdfPCell(new Phrase("(tidak ada)", FONT_NORMAL));
            empty.setColspan(3); empty.setBorder(PdfPCell.NO_BORDER); empty.setPaddingBottom(4f);
            rightTable.addCell(empty);
        }
        SlipGajiTableHelper.addTotalLine(rightTable, "Total Potongan Tambahan", formatRupiah(dto.totalPotonganTambahan()), FONT_BOLD);
        rightCell.addElement(rightTable);
        PdfPCell totalDibayarkanCell = new PdfPCell();
        totalDibayarkanCell.setColspan(2); 
        totalDibayarkanCell.setBorder(PdfPCell.TOP); 
        totalDibayarkanCell.setPadding(6f);
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100); 
        totalTable.setWidths(new float[]{70f, 30f});
        PdfPCell lCell = new PdfPCell(new Phrase("Total Dibayarkan", FONT_BOLD));
        lCell.setBorder(PdfPCell.NO_BORDER); 
        lCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        // Total Dibayarkan bersumber dari penghasilan_bersih_final2 (atau fallback ke penghasilan_bersih_final / kalkulasi subtotal)
        PdfPCell vCell = new PdfPCell(new Phrase(formatRupiah(dto.totalDibayarkan()), FONT_BOLD));
        vCell.setBorder(PdfPCell.NO_BORDER); vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(lCell); totalTable.addCell(vCell);
        totalDibayarkanCell.addElement(totalTable);
        addTable.addCell(leftCell); addTable.addCell(rightCell); addTable.addCell(totalDibayarkanCell);
        addTable.setSpacingAfter(15f);
        document.add(addTable);
    }
    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("Dicetak otomatis pada " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), FONT_SMALL);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);
    }
}
