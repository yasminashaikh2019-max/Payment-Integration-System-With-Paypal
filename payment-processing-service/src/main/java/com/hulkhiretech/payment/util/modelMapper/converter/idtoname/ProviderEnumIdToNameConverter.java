package com.hulkhiretech.payment.util.modelMapper.converter.idtoname;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.ProviderEnum;

@Component
public class ProviderEnumIdToNameConverter
        extends AbstractConverter<Integer, String> {

	@Override
	protected String convert(Integer source) {

	    if (source == null) {
	        return null;
	    }

	    ProviderEnum provider = ProviderEnum.getById(source);

	    if (provider == null) {
	        return null;
	    }

	    return provider.getName();
	}
}