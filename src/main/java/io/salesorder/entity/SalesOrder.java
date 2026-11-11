package io.salesorder.entity;

import io.customer.entity.Customer;
import io.lib.entity.BaseJpaEntity;
import io.lib.service.FormatUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class SalesOrder extends BaseJpaEntity {

    @ManyToOne
    private Customer customer;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(precision = FormatUtil.BIG_DECIMAL_PRECISION, scale = FormatUtil.BIG_DECIMAL_SCALE)
    private BigDecimal amountDue;

    @OneToMany()
    private Set<SalesOrderPayment> payments = new HashSet<>();



    @OneToMany(mappedBy = "salesOrder", fetch = FetchType.LAZY)
    private List<SaleOrderItem> salesOrderItems = new ArrayList<>();

    public BigDecimal getAmountPaid() {
        if (payments == null || payments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return payments.stream()
                .map(SalesOrderPayment::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
