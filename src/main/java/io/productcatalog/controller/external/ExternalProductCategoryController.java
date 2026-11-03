package io.productcatalog.controller.external;

import io.lib.form.BaseFetchForm;
import io.lib.view.PagedEntityApiResponse;
import io.productcatalog.service.ProductCategoryReadService;
import io.productcatalog.view.ProductCategoryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.lib.service.SystemConfig.CUSTOMER_USER_BASE_URL;

@RestController
@RequestMapping(CUSTOMER_USER_BASE_URL + "/product-categories")
public class ExternalProductCategoryController {
    private ProductCategoryReadService productCategoryReadService;

    @GetMapping("list")
    public PagedEntityApiResponse<ProductCategoryView> list(
        @RequestParam(name = "query", required = false)String query,
        @RequestParam(name = "PageNum", required = false, defaultValue = "0")Integer pageNum,
        @RequestParam(name = "pageSize", required = false, defaultValue = "100")Integer pageSize){

        var form = new BaseFetchForm();
        form.setQuery(query);
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);

        var page = productCategoryReadService.listProductCategories(form);
        var views = page.getContent().stream().map(ProductCategoryView::new).toList();
        return new PagedEntityApiResponse<>(page, views);
    }

    @Autowired
    public void setProductCategoryReadService(ProductCategoryReadService service) {
        this.productCategoryReadService = service;
    }
}
