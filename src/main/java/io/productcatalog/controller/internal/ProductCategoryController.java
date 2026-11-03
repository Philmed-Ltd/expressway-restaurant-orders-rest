package io.productcatalog.controller.internal;

import io.lib.form.BaseFetchForm;
import io.lib.service.Message;
import io.lib.view.EntityApiResponse;
import io.lib.view.PagedEntityApiResponse;
import io.productcatalog.form.ProductCategoryEditForm;
import io.productcatalog.form.ProductCategoryRegistrationForm;
import io.productcatalog.service.ProductCategoryEditService;
import io.productcatalog.service.ProductCategoryReadService;
import io.productcatalog.view.ProductCategoryView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static io.lib.service.SystemConfig.INTERNAL_USER_BASE_URL;

@RestController
@RequestMapping(INTERNAL_USER_BASE_URL + "/product-categories")
public class ProductCategoryController {

    private ProductCategoryEditService productCategoryEditService;
    private ProductCategoryReadService productCategoryReadService;

    @PreAuthorize("hasAuthority('REGISTER_PRODUCT_CATEGORY')")
    @PostMapping("register")
    public EntityApiResponse<ProductCategoryView> registerProductCategory(
            @RequestBody @Valid ProductCategoryRegistrationForm form,
            Authentication auth,
            Locale locale) {
        form.setSessionUserId(auth.getName());
        var productCategory = productCategoryEditService.registerProductCategory(form);

        return new EntityApiResponse<>(
                Message.get("product.category.registration.success", locale),
                new ProductCategoryView(productCategory)
        );
    }

    @PreAuthorize("hasAuthority('EDIT_PRODUCT_CATEGORY')")
    @PutMapping("edit/{productCategoryId}")
    public EntityApiResponse<ProductCategoryView> editProductCategory(
            @PathVariable String productCategoryId,
            @RequestBody @Valid ProductCategoryEditForm form,
            Authentication auth,
            Locale locale) {
        form.setSessionUserId(auth.getName());
        var productCategory = productCategoryEditService.editProductCategory(productCategoryId, form);

        return new EntityApiResponse<>(
                Message.get("product.category.edit.success", locale),
                new ProductCategoryView(productCategory)
        );
    }

    @PreAuthorize("hasAuthority('VIEW_PRODUCT_CATEGORY')")
    @GetMapping("list")
    public PagedEntityApiResponse<ProductCategoryView> list(
            @RequestParam(name="query", required = false)String query,
            @RequestParam(name="pageNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(name="pageSize", required = false, defaultValue = "100") Integer pageSize
    ){
        var form = new BaseFetchForm();
        form.setQuery(query);
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);

        var page = productCategoryReadService .listProductCategories(form);
        var views = page.getContent().stream().map(ProductCategoryView::new).toList();
        return new PagedEntityApiResponse<>(page, views);
    }

    @Autowired
    public void setProductCategoryEditService(ProductCategoryEditService service){
        this.productCategoryEditService = service;
    }

    @Autowired
    public void setProductCategoryReadService(ProductCategoryReadService service){
        this.productCategoryReadService = service;
    }

}
