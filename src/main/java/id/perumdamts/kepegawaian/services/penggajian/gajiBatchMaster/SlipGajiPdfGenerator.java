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
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        return new DecimalFormat("Rp #,##0", symbols).format(amount);
    }

    public byte[] generatePdf(SlipGajiDto dto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            PdfPTable kopTable = new PdfPTable(2);
            kopTable.setWidthPercentage(100);
            kopTable.setWidths(new float[]{15f, 85f});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(PdfPCell.BOTTOM);
            logoCell.setBorderWidthBottom(1.5f);
            logoCell.setPaddingBottom(8f);
            try {
                ClassPathResource resource = new ClassPathResource(properties.kop().logoPath());
                if (resource.exists()) {
                    Image img = Image.getInstance(resource.getURL());
                    img.scaleAbsolute(45, 45);
                    logoCell.addElement(img);
                }
            } catch (Exception e) {
                log.warn("Logo slip gaji tidak ditemukan: {}", e.getMessage());
            }

            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(PdfPCell.BOTTOM);
            infoCell.setBorderWidthBottom(1.5f);
            infoCell.setPaddingBottom(8f);
            infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            Paragraph pInstansi = new Paragraph(properties.kop().instansi(), FONT_TITLE);
            pInstansi.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(pInstansi);
            Paragraph pUnit = new Paragraph(properties.kop().unitKerja(), FONT_SUBTITLE);
            pUnit.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(pUnit);
            Paragraph pAddress = new Paragraph(properties.kop().alamat() + " | Telp: " + properties.kop().telepon(), FONT_SMALL);
            pAddress.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(pAddress);

            kopTable.addCell(logoCell);
            kopTable.addCell(infoCell);
            document.add(kopTable);

            Paragraph title = new Paragraph("SLIP GAJI PEGAWAI", FONT_TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(10f);
            document.add(title);

            Paragraph periode = new Paragraph("Periode: " + (dto.periode() != null ? dto.periode() : "-"), FONT_SUBTITLE);
            periode.setAlignment(Element.ALIGN_CENTER);
            periode.setSpacingAfter(10f);
            document.add(periode);

            PdfPTable empTable = new PdfPTable(4);
            empTable.setWidthPercentage(100);
            empTable.setWidths(new float[]{15f, 35f, 15f, 35f});
            addEmpRow(empTable, "Nama", dto.nama(), "Jabatan", dto.namaJabatan());
            addEmpRow(empTable, "NIPAM", dto.nipam(), "Golongan", dto.golongan());
            addEmpRow(empTable, "Bank", dto.bank(), "", "");
            empTable.setSpacingAfter(10f);
            document.add(empTable);

            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{50f, 50f});
            mainTable.addCell(createKomponenBox("PENERIMAAN", dto.penerimaan(), dto.totalPenerimaan()));
            mainTable.addCell(createKomponenBox("POTONGAN", dto.potongan(), dto.totalPotongan()));
            mainTable.setSpacingAfter(10f);
            document.add(mainTable);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{70f, 30f});
            addSummaryRow(summaryTable, "Penerimaan - Potongan", formatRupiah(dto.selisihPenerimaanPotongan()));
            addSummaryRow(summaryTable, "Pembulatan", formatRupiah(dto.pembulatan()));
            addSummaryRow(summaryTable, "Sub Total", formatRupiah(dto.subTotal()));
            summaryTable.setSpacingAfter(10f);
            document.add(summaryTable);

            if ((dto.penerimaanTambahan() != null && !dto.penerimaanTambahan().isEmpty()) ||
                    (dto.potonganTambahan() != null && !dto.potonganTambahan().isEmpty())) {
                PdfPTable addTable = new PdfPTable(2);
                addTable.setWidthPercentage(100);
                addTable.setWidths(new float[]{50f, 50f});
                addTable.addCell(createKomponenBox("PENERIMAAN TAMBAHAN", dto.penerimaanTambahan(), dto.totalPenerimaanTambahan()));
                addTable.addCell(createKomponenBox("POTONGAN TAMBAHAN", dto.potonganTambahan(), dto.totalPotonganTambahan()));
                addTable.setSpacingAfter(10f);
                document.add(addTable);
            }

            PdfPTable finalTable = new PdfPTable(2);
            finalTable.setWidthPercentage(100);
            finalTable.setWidths(new float[]{70f, 30f});
            PdfPCell labelFinal = new PdfPCell(new Paragraph("TOTAL DIBAYARKAN", FONT_TITLE));
            labelFinal.setPadding(8f);
            labelFinal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPCell valFinal = new PdfPCell(new Paragraph(formatRupiah(dto.totalDibayarkan()), FONT_TITLE));
            valFinal.setPadding(8f);
            valFinal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            finalTable.addCell(labelFinal);
            finalTable.addCell(valFinal);
            finalTable.setSpacingAfter(20f);
            document.add(finalTable);

            Paragraph footer = new Paragraph("Dicetak otomatis pada " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), FONT_SMALL);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);
            document.close();
        } catch (Exception e) {
            log.error("Gagal generate PDF slip gaji: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal generate PDF slip gaji", e);
        }
        return baos.toByteArray();
    }

    private void addEmpRow(PdfPTable t, String l1, String v1, String l2, String v2) {
        PdfPCell c1 = new PdfPCell(new Phrase(l1, FONT_BOLD)); c1.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c2 = new PdfPCell(new Phrase(": " + (v1 != null ? v1 : "-"), FONT_NORMAL)); c2.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c3 = new PdfPCell(new Phrase(l2, FONT_BOLD)); c3.setBorder(PdfPCell.NO_BORDER);
        PdfPCell c4 = new PdfPCell(new Phrase(l2.isEmpty() ? "" : ": " + (v2 != null ? v2 : "-"), FONT_NORMAL)); c4.setBorder(PdfPCell.NO_BORDER);
        t.addCell(c1); t.addCell(c2); t.addCell(c3); t.addCell(c4);
    }

    private PdfPCell createKomponenBox(String title, List<SlipGajiKomponenItemDto> items, Double total) {
        PdfPTable inner = new PdfPTable(2);
        inner.setWidthPercentage(100);
        try { inner.setWidths(new float[]{70f, 30f}); } catch (Exception ignored) {}
        PdfPCell tCell = new PdfPCell(new Phrase(title, FONT_BOLD)); tCell.setColspan(2); tCell.setPadding(4f);
        inner.addCell(tCell);
        if (items != null) {
            for (SlipGajiKomponenItemDto item : items) {
                inner.addCell(new PdfPCell(new Phrase(item.nama() != null ? item.nama() : "-", FONT_NORMAL)));
                PdfPCell vCell = new PdfPCell(new Phrase(formatRupiah(item.nilai()), FONT_NORMAL));
                vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                inner.addCell(vCell);
            }
        }
        PdfPCell tLbl = new PdfPCell(new Phrase("Total " + title, FONT_BOLD));
        PdfPCell tVal = new PdfPCell(new Phrase(formatRupiah(total), FONT_BOLD)); tVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        inner.addCell(tLbl); inner.addCell(tVal);
        PdfPCell wrap = new PdfPCell(inner); wrap.setPadding(4f);
        return wrap;
    }

    private void addSummaryRow(PdfPTable t, String label, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, FONT_BOLD)); c1.setHorizontalAlignment(Element.ALIGN_RIGHT); c1.setPadding(4f);
        PdfPCell c2 = new PdfPCell(new Phrase(value, FONT_BOLD)); c2.setHorizontalAlignment(Element.ALIGN_RIGHT); c2.setPadding(4f);
        t.addCell(c1); t.addCell(c2);
    }
}
