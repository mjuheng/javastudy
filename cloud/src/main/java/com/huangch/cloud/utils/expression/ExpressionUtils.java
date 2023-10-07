package com.huangch.cloud.utils.expression;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.List;
import java.util.Map;

/**
 * @author huangch
 * @date 2023-09-25
 */
public class ExpressionUtils {

    /**
     * 执行EL表达式判断结果
     *
     * @param expression EL表达式
     * @param variables  判断具体值
     * @return 判断结果
     */
    public static boolean elResult(String expression, Map<String, Object> variables) {
        Expression exp = AviatorEvaluator.compile(expression);
        final Object execute = exp.execute(variables);
        return Boolean.parseBoolean(String.valueOf(execute));
    }

    /**
     * 获取EL表达式的所有KEY值
     *
     * @param expression EL表达式
     * @return KEY值列表
     */
    public static List<String> elKeys(String expression) {
        Expression exp = AviatorEvaluator.compile(expression);
        return exp.getVariableFullNames();
    }
}
