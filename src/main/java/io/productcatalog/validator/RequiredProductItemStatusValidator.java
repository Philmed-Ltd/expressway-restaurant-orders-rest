package io.productcatalog.validator;

import io.productcatalog.entity.ProductStatus;
import io.productcatalog.service.ProductItemReadService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class RequiredProductItemStatusValidator implements ConstraintValidator<RequiredProductItemStatus, String> {

    private List<ProductStatus> statuses;

    private ProductItemReadService productItemReadService;

    @Override
    public void initialize(RequiredProductItemStatus constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.statuses = List.of(constraintAnnotation.status());
    }

    @Override
    public boolean isValid(String productItemId, ConstraintValidatorContext ctx) {
        var productItem = productItemReadService.findByEntityId(productItemId);
        return statuses.contains(productItem.getStatus());
    }

    @Autowired
    public void setProductItemReadService(ProductItemReadService service) {this.productItemReadService = service;}
}