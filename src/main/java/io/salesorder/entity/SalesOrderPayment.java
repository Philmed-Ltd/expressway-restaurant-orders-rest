package io.salesorder.entity;

import io.lib.entity.BaseJpaEntity;
import io.lib.service.FormatUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "sales_order_payment",
        indexes = {
                @Index(name = "idx_sales_order_payment_order_id", columnList = "sales_order_id")
        }
)
public class SalesOrderPayment extends BaseJpaEntity {

    @ManyToOne
    private SalesOrder salesOrder;

    private String salesOrderEntityId;

    @Column(precision = FormatUtil.BIG_DECIMAL_PRECISION, scale = FormatUtil.BIG_DECIMAL_SCALE)
    private BigDecimal amount;

    private String transactionId;

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
        if (salesOrder != null) {
            this.salesOrderEntityId = salesOrder.getEntityId();
        } else {
            this.salesOrderEntityId = null;
        }
    }
}
