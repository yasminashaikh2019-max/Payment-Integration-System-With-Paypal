package com.hulkhiretech.payment.constant;

import lombok.Getter;

@Getter
public enum ProviderEnum {

    PAYPAL(1,"PAYPAL");
    private final Integer id;
    private final String name;

    ProviderEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public static ProviderEnum getById(Integer id) {

        if (id == null) {
            return null;
        }

        for (ProviderEnum provider : values()) {
            if (id.equals(provider.id)) {
                return provider;
            }
        }

        return null;
    }

    public static ProviderEnum getByName(String name) {

        if (name == null) {
            return null;
        }

        for (ProviderEnum provider : values()) {
            if (name.equalsIgnoreCase(provider.name)) {
                return provider;
            }
        }

        return null;
    }
}
