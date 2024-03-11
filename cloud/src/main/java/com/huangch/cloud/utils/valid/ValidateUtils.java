package com.huangch.cloud.utils.valid;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author huangch
 * @since 2023-12-28
 */
@SuppressWarnings("AlibabaConstantFieldShouldBeUpperCase")
public class ValidateUtils {

    public final static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        validatorFactory.close();
    }

    /**
     * 校验参数
     *
     * @param obj 被校验的实例
     * @throws RuntimeException 不满足校验时返回的异常信息
     */
    public static void valid(Object obj) throws RuntimeException {
        Set<ConstraintViolation<Object>> validates = validator.validate(obj);

        for (ConstraintViolation<Object> validate : validates) {
            String errMessage = validate.getMessage();
            throw new RuntimeException(errMessage);
        }
    }
}
