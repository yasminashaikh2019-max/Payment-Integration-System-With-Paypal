package com.hulkhiretech.payment.util.modelMapper.converter.nametoid;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.TxnStatusEnum;

@Component
public class TxnStatusEnumConverter extends AbstractConverter<String, Integer> {

    @Override
    protected Integer convert(String source) {

        if (source == null) {
            return null;
        }

        TxnStatusEnum status = TxnStatusEnum.getByName(source);

        return status != null ? status.getId() : null;
    }
}