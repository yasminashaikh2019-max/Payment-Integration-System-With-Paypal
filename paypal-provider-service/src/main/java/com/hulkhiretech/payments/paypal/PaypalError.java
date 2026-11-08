package com.hulkhiretech.payments.paypal;

import java.util.List;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaypalError {

    @JsonProperty("error")
    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("name")
    private String name;

    @JsonProperty("message")
    private String message;

    @JsonProperty("details")
    private List<Detail> details;

    @Data
    public static class Detail {

        @JsonProperty("field")
        private String field;

        @JsonProperty("value")
        private String value;

        @JsonProperty("location")
        private String location;

        @JsonProperty("issue")
        private String issue;

        @JsonProperty("description")
        private String description;
    }
}