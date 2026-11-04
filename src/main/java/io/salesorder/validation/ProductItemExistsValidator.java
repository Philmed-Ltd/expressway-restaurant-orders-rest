package io.salesorder.validation;

import io.productcatalog.service.ProductItemReadService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductItemExistsValidator implements ConstraintValidator<ProductItemExists, String> {

    private ProductItemReadService productItemReadService;

    @Override
    public boolean isValid(String productItemEntityId, ConstraintValidatorContext context) {
        if (productItemEntityId == null || productItemEntityId.isBlank()) {
            return true;
        }
        return productItemReadService.findByEntityId(productItemEntityId) != null;
    }

    @Autowired
    public void setProductItemReadService(ProductItemReadService service) {
        this.productItemReadService = service;
    }
}
