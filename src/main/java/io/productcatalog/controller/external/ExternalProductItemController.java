package io.productcatalog.controller.external;

import io.lib.view.PagedEntityApiResponse;
import io.productcatalog.entity.ProductStatus;
import io.productcatalog.form.ProductItemFetchForm;
import io.productcatalog.service.ProductItemReadService;
import io.productcatalog.view.ProductItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.lib.service.SystemConfig.CUSTOMER_USER_BASE_URL;

@RestController
@RequestMapping(CUSTOMER_USER_BASE_URL + "/product-items")
public class ExternalProductItemController {
    private ProductItemReadService productItemReadService;

    @GetMapping("list")
    public PagedEntityApiResponse<ProductItemView> list(
        @RequestParam(name = "query", required = false)String query,
        @RequestParam(name = "productCategoryId", required = false)String productCategoryId,
        @RequestParam(name = "PageNum", required = false, defaultValue = "0")Integer pageNum,
        @RequestParam(name = "pageSize", required = false, defaultValue = "100")Integer pageSize){

        var form = new ProductItemFetchForm();
        form.setProductStatus(ProductStatus.ACTIVE);
        form.setQuery(query);
        form.setProductCategoryId(productCategoryId);
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);

        var page = productItemReadService.listProductItems(form);
        var views = page.getContent().stream().map(ProductItemView::new).toList();
        return new PagedEntityApiResponse<>(page, views);
    }

    @Autowired
    public void setProductItemReadService(ProductItemReadService service) {
        this.productItemReadService = service;
    }
}
