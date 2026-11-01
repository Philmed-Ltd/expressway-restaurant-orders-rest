package io.productcatalog.service;

import io.activitylog.form.CreateActivityLogForm;
import io.lib.exception.CommonRuntimeException;
import io.lib.exception.ExceptionType;
import io.lib.service.BaseJpaRepoEditService;
import io.productcatalog.entity.ProductItem;
import io.productcatalog.entity.ProductStatus;
import io.productcatalog.form.ProductItemEditForm;
import io.productcatalog.form.ProductItemRegistrationForm;
import io.productcatalog.form.ProductStatusEditingForm;
import io.productcatalog.repository.ProductItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductItemEditService extends BaseJpaRepoEditService<ProductItem, ProductItemRepository> {
    private ProductCategoryReadService productCategoryReadService;

    public ProductItem registerItem(ProductItemRegistrationForm form) {
        var productCategory = productCategoryReadService.findByEntityId(form.getProductCategoryEntityId());

        var  productItem = new ProductItem();
        productItem.setName(form.getName());
        productItem.setDescription(form.getDescription());
        productItem.setPrice(form.getPrice());
        productItem.setImageUrl(form.getImageUrl());
        productItem.setProductCategory(productCategory);
        productItem.setCreatedByEntityId(form.getSessionUserId());

        productItem = save(productItem, form.getSessionUserId());

        var activityLogForm = new CreateActivityLogForm();
        activityLogForm.setOwningEntityId(productItem.getEntityId());
        activityLogForm.setAction("Product item creation");
        activityLogForm.setSessionUserId(form.getSessionUserId());
        activityLogQueuingService.enqueueActivityLog(activityLogForm);

        return productItem;
    }

    public ProductItem updateItem(String productItemId, ProductItemEditForm form) {
        checkNameExists(productItemId, form.getName());

        var productCategory = productCategoryReadService.findByEntityId(form.getProductCategoryEntityId());

        var  productItem = findByEntityId(productItemId);
        productItem.setName(form.getName());
        productItem.setDescription(form.getDescription());
        productItem.setPrice(form.getPrice());
        productItem.setImageUrl(form.getImageUrl());
        productItem.setProductCategory(productCategory);
        productItem.setCreatedByEntityId(form.getSessionUserId());

        productItem = save(productItem, form.getSessionUserId());

        var activityLogForm = new CreateActivityLogForm();
        activityLogForm.setOwningEntityId(productItem.getEntityId());
        activityLogForm.setAction("Product item update");
        activityLogForm.setSessionUserId(form.getSessionUserId());
        activityLogQueuingService.enqueueActivityLog(activityLogForm);

        return productItem;

    }

    private void checkNameExists(String productItemId, String name) {
        var specification = repository.notDeleted()
                .and(repository.nameIs(name))
                .and(repository.entityIdNot(productItemId));

        boolean exists = repository.exists(specification);

        if (exists) {
            throw new CommonRuntimeException(ExceptionType.BAD_REQUEST, "error.duplicate.name");
        }
    }

    public ProductItem markProductStatusAsBlocked(String productItemId, ProductStatusEditingForm editingForm) {
        ProductStatus invalidStatus = ProductStatus.BLOCKED;
        return changingTheProductStatus(productItemId, editingForm, invalidStatus);
    }

    public ProductItem markProductStatusAsActive(String productItemId, ProductStatusEditingForm editingForm) {
        ProductStatus invalidStatus = ProductStatus.ACTIVE;
        return changingTheProductStatus(productItemId, editingForm, invalidStatus);
    }

    private ProductItem changingTheProductStatus(String productItemId, ProductStatusEditingForm editForm, ProductStatus invalidCurrentStatus) {
        var productItem = findByEntityId(productItemId);

        if (productItem.getStatus() == invalidCurrentStatus) {
            throw new CommonRuntimeException(ExceptionType.BAD_REQUEST, "error.invalid.status");
        }

        var updateProductItem = updateProductStatus(productItemId, editForm.getStatus(), editForm.getSessionUserId());

        var activityLogForm = new CreateActivityLogForm();
        activityLogForm.setOwningEntityId(productItem.getEntityId());
        activityLogForm.setAction("Product item status update");
        activityLogForm.setSessionUserId(editForm.getSessionUserId());

        activityLogQueuingService.enqueueActivityLog(activityLogForm);

        return updateProductItem;
    }

    public ProductItem updateProductStatus(String productItemId, ProductStatus newStatus, String statusUpdatedByEntityId) {
        var productItem = findByEntityId(productItemId);

        productItem.setStatus(newStatus);
        productItem = save(productItem, statusUpdatedByEntityId);

        return productItem;
    }

    @Autowired
    public void setProductCategoryReadService(ProductCategoryReadService productCategoryReadService) {
        this.productCategoryReadService = productCategoryReadService;
    }
}
