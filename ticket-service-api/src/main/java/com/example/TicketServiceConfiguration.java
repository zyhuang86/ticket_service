package com.example;

import io.dropwizard.Configuration;

import javax.validation.Valid;

public class TicketServiceConfiguration extends Configuration {

    @Valid
    private Integer maxOnHoldDuration;

    public Integer getMaxOnHoldDuration() {
        return maxOnHoldDuration;
    }

    public void setMaxOnHoldDuration(Integer maxOnHoldDuration) {
        this.maxOnHoldDuration = maxOnHoldDuration;
    }
}
