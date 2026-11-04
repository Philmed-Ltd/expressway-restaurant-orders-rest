package io.salesorder.entity;

import io.lib.entity.BaseJpaEntity;
import io.productcatalog.entity.ProductItem;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
    indexes = {
        @Index(name = "idx_sales_order_entity_id", columnList = "salesOrderEntityId"),
        @Index(name = "idx_product_item_entity_id", columnList = "productItemEntityId")
    }
)
public class SaleOrderItem extends BaseJpaEntity {
    @ManyToOne
    private SalesOrder salesOrder;

    private String salesOrderEntityId;

    @ManyToOne
    private ProductItem productItem;

    private Double quantity;

    private String productItemEntityId;

    private BigDecimal productPrice;

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
        if(this.salesOrder != null){
            setSalesOrderEntityId(this.salesOrder.getEntityId());
        }
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
        if(this.productItem != null){
            setProductItemEntityId(this.productItem.getEntityId());
        }
    }
}
