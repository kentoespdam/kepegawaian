package id.perumdamts.kepegawaian.dto.penggajian.gajiKpi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fesod.sheet.annotation.ExcelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GajiKpiExcelRow {
    @ExcelProperty(index = 3)
    private String no;

    @ExcelProperty(index = 4)
    private String periode;

    @ExcelProperty(index = 5)
    private String nipam;

    @ExcelProperty(index = 6)
    private String nama;

    @ExcelProperty(index = 7)
    private Double tunkin;

    @ExcelProperty(index = 8)
    private Double pph21Ter;
}
