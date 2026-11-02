package io.productcatalog.validator;

import io.productcatalog.entity.ProductItem;
import io.productcatalog.entity.ProductStatus;
import org.springframework.stereotype.Component;


@Component
public class ProductStatusUpdateValidator {

    public void validateStatusToBlocked(ProductItem productItem) {
        if (productItem.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalStateException("Only active product items can be blocked.");
        }
    }

    public void validateStatusToActive(ProductItem productItem) {
        if (productItem.getStatus() != ProductStatus.BLOCKED) {
            throw new IllegalStateException("Only blocked product items can be activated.");
        }
    }
}
