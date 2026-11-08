
package com.hulkhiretech.payments.paypal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class PaypalOrderResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("links")
    private List<Link> links;
@Data
    public static class Link {

        @JsonProperty("href")
        private String href;

        @JsonProperty("rel")
        private String rel;

        @JsonProperty("method")
        private String method;
    }
}

