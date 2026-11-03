package io.productcatalog.controller;

import io.lib.service.Message;
import io.lib.view.EntityApiResponse;
import io.lib.view.PagedEntityApiResponse;
import io.productcatalog.entity.ProductStatus;
import io.productcatalog.form.ProductItemEditForm;
import io.productcatalog.form.ProductItemFetchForm;
import io.productcatalog.form.ProductItemRegistrationForm;
import io.productcatalog.service.ProductItemEditService;
import io.productcatalog.service.ProductItemReadService;
import io.productcatalog.service.ProductStatusUpdateService;
import io.productcatalog.validator.RequiredProductItemStatus;
import io.productcatalog.view.ProductItemView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static io.lib.service.SystemConfig.INTERNAL_USER_BASE_URL;

@RestController
@RequestMapping(INTERNAL_USER_BASE_URL + "/product-items")
public class ProductItemController {
    private ProductItemEditService productItemEditService;
    private ProductItemReadService productItemReadService;
    private ProductStatusUpdateService productStatusUpdateService;

    @PreAuthorize("hasAuthority('REGISTER_PRODUCT_ITEM')")
    @PostMapping("register")
    public EntityApiResponse<ProductItemView> registerItem(
            @RequestBody @Valid ProductItemRegistrationForm form,
            Authentication auth,
            Locale locale) {
        form.setSessionUserId(auth.getName());
        var product = productItemEditService.registerItem(form);

        return new EntityApiResponse<>(
                Message.get("product.item.registration.success", locale),
                new ProductItemView(product)
        );
    }

    @PreAuthorize("hasAuthority('EDIT_PRODUCT_ITEM')")
    @PutMapping("edit/{productItemId}")
    public EntityApiResponse<ProductItemView> editItem(
            @PathVariable String productItemId,
            @RequestBody @Valid ProductItemEditForm form,
            Authentication auth,
            Locale locale){
        form.setSessionUserId(auth.getName());
        var product = productItemEditService.updateItem(productItemId, form);
        return new EntityApiResponse<>(
                Message.get("product.item.update.success", locale),
                new ProductItemView(product)
        );
    }

    @PreAuthorize("hasAuthority('VIEW_PRODUCT_ITEM')")
    @GetMapping("list")
    public PagedEntityApiResponse<ProductItemView> list(
            @RequestParam(name = "query", required = false)String query,
            @RequestParam(name = "productCategoryEntityId", required = false)String productCategoryEntityId,
            @RequestParam(name = "productStatus", required = false)ProductStatus productStatus,
            @RequestParam(name = "PageNum", required = false, defaultValue = "0")Integer pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "100")Integer pageSize,
            Authentication auth
    ){
        var form = new ProductItemFetchForm();
        form.setQuery(query);
        form.setProductCategoryEntityId(productCategoryEntityId);
        form.setProductStatus(productStatus);
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);
        form.setSessionUserId(auth.getName());

        var page = productItemReadService.listProductItems(form);
        var views = page.getContent().stream().map(ProductItemView::new).toList();
        return new PagedEntityApiResponse<>(page, views);
    }

    @PreAuthorize("hasAuthority('VIEW_PRODUCT_ITEM')")
    @GetMapping("fetch/{productItemId}")
    public EntityApiResponse<ProductItemView> fetch(
            @PathVariable String productItemId
    ){
        var product = productItemReadService.findByEntityId(productItemId);
        return new EntityApiResponse<>(new ProductItemView(product));
    }

    @PreAuthorize("hasAuthority('UPDATE_PRODUCT_STATUS_TO_BLOCKED')")
    @PutMapping("update-product-status-to-blocked/{productItemId}")
    public EntityApiResponse<ProductItemView> updateProductStatusToBlocked(
            @PathVariable
            @RequiredProductItemStatus(
                    status = { ProductStatus.ACTIVE },
                    message = "error.product.not.active")
            String productItemId,
            Authentication auth,
            Locale locale) {
        var item =  productStatusUpdateService.updateProductStatusToBlocked(productItemId, auth.getName());
        return new EntityApiResponse<>(
                Message.get("product.item.update.success", locale),
                new ProductItemView(item)
        );
    }
    @PreAuthorize("hasAuthority('UPDATE_PRODUCT_STATUS_TO_ACTIVE')")
    @PutMapping("update-product-status-to-active/{productItemId}")
    public EntityApiResponse<ProductItemView> updateProductStatusToActive(
            @PathVariable
            @RequiredProductItemStatus(
                    status = { ProductStatus.BLOCKED },
                    message = "error.product.not.blocked")
            String productItemId,
            Authentication auth,
            Locale locale) {

        var item = productStatusUpdateService.updateProductStatusToActive(productItemId, auth.getName());
        return new EntityApiResponse<>(
                Message.get("product.item.update.success", locale),
                new ProductItemView(item)
        );
    }

    @Autowired
    public void setProductItemEditService(ProductItemEditService productItemEditService) {this.productItemEditService = productItemEditService;}

    @Autowired
    public void setProductItemReadService(ProductItemReadService productItemReadService) {this.productItemReadService = productItemReadService;}

    @Autowired
    public void setProductStatusUpdateService(ProductStatusUpdateService productStatusUpdateService) {this.productStatusUpdateService = productStatusUpdateService;}
}
