package com.hulkhiretech.payment.util.modelMapper.converter.idtoname;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.PaymentTypeEnum;

@Component
public class PaymentTypeEnumIdToNameConverter
        extends AbstractConverter<Integer, String> {

    @Override
    protected String convert(Integer source) {

        if (source == null) {
            return null;
        }

        PaymentTypeEnum paymentType =
                PaymentTypeEnum.getById(source);

        return paymentType != null
                ? paymentType.getName()
                : null;
    }
}