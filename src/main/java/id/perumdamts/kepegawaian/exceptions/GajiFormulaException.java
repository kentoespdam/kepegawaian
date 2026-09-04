package id.perumdamts.kepegawaian.exceptions;

public class GajiFormulaException extends RuntimeException {
    public GajiFormulaException(String formula, Throwable cause) {
        super("Formula tidak valid: '" + formula + "'", cause);
    }
}
