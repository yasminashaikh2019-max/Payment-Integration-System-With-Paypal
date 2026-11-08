package com.hulkhiretech.payment.util.modelMapper.converter.idtoname;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.TxnStatusEnum;

@Component
public class TxnStatusEnumIdToNameConverter
        extends AbstractConverter<Integer, String> {

    @Override
    protected String convert(Integer source) {

        if (source == null) {
            return null;
        }

        TxnStatusEnum txnStatus =
                TxnStatusEnum.getById(source);

        return txnStatus != null
                ? txnStatus.getName()
                : null;
    }
}