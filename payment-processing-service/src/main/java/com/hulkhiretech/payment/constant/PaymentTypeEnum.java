package com.hulkhiretech.payment.constant;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {

    SALE(1,"SALE");
    private final Integer id;
    private final String name;

    PaymentTypeEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public static PaymentTypeEnum getById(Integer id) {

        if (id == null) {
            return null;
        }

        for (PaymentTypeEnum paymentType : values()) {
            if (id.equals(paymentType.id)) {
                return paymentType;
            }
        }

        return null;
    }

    public static PaymentTypeEnum getByName(String name) {

        if (name == null) {
            return null;
        }

        for (PaymentTypeEnum paymentType : values()) {
            if (name.equalsIgnoreCase(paymentType.name)) {
                return paymentType;
            }
        }

        return null;
    }

}