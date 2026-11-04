package io.salesorder.entity;

import io.customer.entity.Customer;
import io.lib.entity.BaseJpaEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SalesOrder extends BaseJpaEntity {

    @ManyToOne
    private Customer customer;

    @Embedded
    private DeliveryAddress deliveryAddress;

}
