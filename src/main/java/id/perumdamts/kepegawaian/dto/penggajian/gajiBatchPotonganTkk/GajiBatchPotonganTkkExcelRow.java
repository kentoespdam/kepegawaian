package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTkk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fesod.sheet.annotation.ExcelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GajiBatchPotonganTkkExcelRow {
    @ExcelProperty(index = 0)
    private String no;

    @ExcelProperty(index = 1)
    private String nipam;

    @ExcelProperty(index = 2)
    private String nama;

    @ExcelProperty(index = 3)
    private Double potongan;
}
