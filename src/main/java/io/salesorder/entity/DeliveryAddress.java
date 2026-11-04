package io.salesorder.entity;

import jakarta.persistence.Embeddable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DeliveryAddress {
    @NotBlank
    private String address;
    private String instructions;
    private String latitude;
    private String longitude;
}

