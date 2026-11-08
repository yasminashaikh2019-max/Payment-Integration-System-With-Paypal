package com.hulkhiretech.payment.util.modelMapper.converter.nametoid;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.ProviderEnum;

@Component
public class ProviderEnumConverter extends AbstractConverter<String, Integer> {

    @Override
    protected Integer convert(String source) {

        if (source == null) {
            return null;
        }

        ProviderEnum provider = ProviderEnum.getByName(source);

        return provider != null ? provider.getId() : null;
    }
}