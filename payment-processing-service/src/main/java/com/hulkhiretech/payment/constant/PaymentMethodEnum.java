package com.hulkhiretech.payment.constant;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {

    APM(1,"APM");

    private final Integer id;
    private final String name;

    PaymentMethodEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public static PaymentMethodEnum getById(Integer id) {

        if (id == null) {
            return null;
        }

        for (PaymentMethodEnum paymentMethod : values()) {
            if (id.equals(paymentMethod.id)) {
                return paymentMethod;
            }
        }

        return null;
    }

    public static PaymentMethodEnum getByName(String name) {

        if (name == null) {
            return null;
        }

        for (PaymentMethodEnum paymentMethod : values()) {
            if (name.equalsIgnoreCase(paymentMethod.name)) {
                return paymentMethod;
            }
        }

        return null;
    }
}