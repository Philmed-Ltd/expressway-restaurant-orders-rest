package io.salesorder.form;

import io.salesorder.validation.ProductItemExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleOrderItemForm {
    @NotBlank(message = "product.item.id.required")
    @ProductItemExists
    private String productItemEntityId;

    @NotNull
    @Positive(message = "product.item.quantity.positive")
    private Double quantity;
}
