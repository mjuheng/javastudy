package com.huangch.cloud.utils.expression;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Map<String, Object> parseValues(String expression) {
        Map<String, Object> values = new HashMap<>();

        // 使用正则表达式匹配变量名和对应的值
        Pattern pattern = Pattern.compile("(\\w+)\\s*==\\s*('([^']*)'|\\b\\d+\\b)");
        Matcher matcher = pattern.matcher(expression);

        // 遍历匹配结果，并将变量名和值添加到Map中
        while (matcher.find()) {
            String variable = matcher.group(1);
            String valueStr = matcher.group(3);
            Object value;
            if (valueStr != null) {
                value = valueStr;
            } else {
                value = Integer.parseInt(matcher.group(2));
            }
            values.put(variable, value);
        }

        return values;
    }
}
