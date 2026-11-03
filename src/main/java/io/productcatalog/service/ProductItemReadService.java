package io.productcatalog.service;

import io.lib.service.BaseJpaRepoReadService;
import io.micrometer.common.util.StringUtils;
import io.productcatalog.entity.ProductItem;
import io.productcatalog.form.ProductItemFetchForm;
import io.productcatalog.repository.ProductItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ProductItemReadService extends BaseJpaRepoReadService<ProductItem, ProductItemRepository> {

    public Page<ProductItem> listProductItems(ProductItemFetchForm form) {
        var spec = repository.notDeleted();

        if(StringUtils.isNotBlank(form.getQuery())){
            spec = spec.and(repository.nameLike(form.getQuery()));
        }

        if(form.getProductCategoryId() != null){
            spec = spec.and(repository.productCategoryIdIs(form.getProductCategoryId()));
        }

        if(form.getProductStatus() != null){
            spec = spec.and(repository.productStatusIs(form.getProductStatus()));
        }

        return repository.findAll(spec, repository.defaultPageable(form));
    }
}
