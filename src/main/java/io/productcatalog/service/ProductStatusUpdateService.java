package io.productcatalog.service;

import io.lib.service.BaseJpaRepoEditService;
import io.productcatalog.entity.ProductItem;
import io.productcatalog.entity.ProductStatus;
import io.productcatalog.repository.ProductItemRepository;
import io.productcatalog.validator.ProductStatusUpdateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductStatusUpdateService extends BaseJpaRepoEditService<ProductItem, ProductItemRepository> {
    private ProductItemReadService productItemReadService;
    private ProductStatusUpdateValidator productStatusUpdateValidator;

    public void updateProductStatusToBlocked(String productItemEntityId, String currentUserEntityId) {
        var productItem = productItemReadService.findByEntityId(productItemEntityId);

        productStatusUpdateValidator.validateStatusToBlocked(productItem);
        productItem.setStatus(ProductStatus.BLOCKED);
        save(productItem, currentUserEntityId);
    }

    public void updateProductStatusToActive(String productItemEntityId, String currentUserEntityId) {
        var productItem = productItemReadService.findByEntityId(productItemEntityId);

        productStatusUpdateValidator.validateStatusToActive(productItem);
        productItem.setStatus(ProductStatus.ACTIVE);
        save(productItem, currentUserEntityId);
    }


    @Autowired
    public void setProductItemReadService(ProductItemReadService productItemReadService) {this.productItemReadService = productItemReadService;}

    @Autowired
    public void setProductStatusUpdateValidator(ProductStatusUpdateValidator productStatusUpdateValidator) {this.productStatusUpdateValidator = productStatusUpdateValidator;}
}
