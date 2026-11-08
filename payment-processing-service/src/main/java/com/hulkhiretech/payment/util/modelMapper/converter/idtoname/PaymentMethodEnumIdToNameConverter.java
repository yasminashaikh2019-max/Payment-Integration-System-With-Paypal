package com.hulkhiretech.payment.util.modelMapper.converter.idtoname;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.PaymentMethodEnum;

@Component
public class PaymentMethodEnumIdToNameConverter
        extends AbstractConverter<Integer, String> {

    @Override
    protected String convert(Integer source) {

        if (source == null) {
            return null;
        }

        PaymentMethodEnum paymentMethod =
                PaymentMethodEnum.getById(source);

        return paymentMethod != null
                ? paymentMethod.getName()
                : null;
    }
}