package com.huangch.cloud.utils.enums;

/**
 * @author huangch
 * @since 2025-07-14
 */
public interface BaseCodeNameEnum {

    String getCode();

    String getName();

    static <E extends Enum<E> & BaseCodeNameEnum> E getByCode(Class<E> enumClass, String code) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    static <E extends Enum<E> & BaseCodeNameEnum> String getNameByCode(Class<E> enumClass, String code) {
        BaseCodeNameEnum codeNameEnum = getByCode(enumClass, code);
        return codeNameEnum == null ? null : codeNameEnum.getName();
    }
}
