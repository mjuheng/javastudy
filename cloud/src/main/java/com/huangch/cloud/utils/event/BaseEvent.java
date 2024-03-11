package com.huangch.cloud.utils.event;

import lombok.Data;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * @author huangch
 * @since 2024-02-26
 */
@Data
public class BaseEvent<T> implements ResolvableTypeProvider {

    private T data;

    public BaseEvent(T data) {
        this.data = data;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getData()));
    }


}
