package io.productcatalog.service;

import io.lib.service.BaseJpaRepoEditService;
import io.productcatalog.entity.ProductItem;
import io.productcatalog.entity.ProductStatus;
import io.productcatalog.repository.ProductItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductStatusUpdateService extends BaseJpaRepoEditService<ProductItem, ProductItemRepository> {
    private ProductItemReadService productItemReadService;

    public ProductItem updateProductStatusToBlocked(String productItemEntityId, String currentUserEntityId) {
        var productItem = productItemReadService.findByEntityId(productItemEntityId);
        productItem.setStatus(ProductStatus.BLOCKED);
        return save(productItem, currentUserEntityId);
    }

    public ProductItem updateProductStatusToActive(String productItemEntityId, String currentUserEntityId) {
        var productItem = productItemReadService.findByEntityId(productItemEntityId);
        productItem.setStatus(ProductStatus.ACTIVE);
        return save(productItem, currentUserEntityId);
    }


    @Autowired
    public void setProductItemReadService(ProductItemReadService productItemReadService) {this.productItemReadService = productItemReadService;}
}
