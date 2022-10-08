package pers.kun.publish.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Objects;


/**
 * @author : qihang.liu
 * @date 2021-05-16
 */
@Slf4j
public class CalculateUtil {
    public static boolean isMathExpr(String s) {
        if (null == s || s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            //skip
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '.' || Character.isSpaceChar(c)) {
                continue;
            }
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String calculate(String s) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(s);
            return Objects.requireNonNull(exp.getValue()).toString();
        } catch (Exception e) {
            log.error("Calculate error, expression is {}", s);
            throw e;
        }
    }
}
