package com.hulkhiretech.payment.util.modelMapper.converter.nametoid;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.PaymentTypeEnum;

@Component
public class PaymentTypeEnumConverter extends AbstractConverter<String, Integer> {

    @Override
    protected Integer convert(String source) {

        if (source == null) {
            return null;
        }

        PaymentTypeEnum paymentType = PaymentTypeEnum.getByName(source);

        return paymentType != null ? paymentType.getId() : null;
    }
}