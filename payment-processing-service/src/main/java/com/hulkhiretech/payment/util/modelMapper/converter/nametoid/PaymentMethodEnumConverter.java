package com.hulkhiretech.payment.util.modelMapper.converter.nametoid;


import org.modelmapper.AbstractConverter;

import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.PaymentMethodEnum;

@Component
public class PaymentMethodEnumConverter extends AbstractConverter<String, Integer> {

    @Override
    protected Integer convert(String source) {

        if (source == null) {
            return null;
        }

        return PaymentMethodEnum.getByName(source).getId();
    }
}