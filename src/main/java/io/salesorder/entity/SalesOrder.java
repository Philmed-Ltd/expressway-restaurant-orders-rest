package io.salesorder.entity;

import io.customer.entity.Customer;
import io.lib.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class SalesOrder extends BaseJpaEntity {

    @ManyToOne
    private Customer customer;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @OneToMany(mappedBy = "salesOrder", fetch = FetchType.LAZY)
    private List<SaleOrderItem> salesOrderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.UNPAID;
}
