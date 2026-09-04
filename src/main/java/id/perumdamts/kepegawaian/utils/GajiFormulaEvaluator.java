package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.exceptions.GajiFormulaException;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Evaluator formula gaji — wrapper exp4j. Formula dinamis dari DB
 * (GajiKomponen.formula), kode komponen sebagai variabel.
 */
@Component
public class GajiFormulaEvaluator {

    public double evaluate(String formula, Map<String, Double> vars) {
        if (formula == null || formula.isBlank())
            return 0.0;
        try {
            // exp4j 0.4.8 case-sensitive utk nama fungsi; formula seed pakai CEIL(...)
            // huruf besar → normalisasi token fungsi ceil ke lowercase dulu.
            String normalized = formula.replaceAll("(?i)\\bceil\\b", "ceil");
            Expression expression = new ExpressionBuilder(normalized)
                    .function(ceil())
                    .variables(vars.keySet())
                    .build();
            vars.forEach(expression::setVariable);
            return expression.evaluate();
        } catch (RuntimeException ex) {
            throw new GajiFormulaException(formula, ex);
        }
    }

    private static Function ceil() {
        return new Function("ceil", 1) {
            @Override
            public double apply(double... args) {
                return Math.ceil(args[0]);
            }
        };
    }
}
