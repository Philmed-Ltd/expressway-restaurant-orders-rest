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
        var specification = repository.notDeleted();

        if(StringUtils.isNotBlank(form.getQuery())){
            specification = specification.and(repository.nameLike(form.getQuery()));
        }

        if(form.getProductCategoryEntityId() != null){
            specification = specification.and(repository.productCategoryEntityIdIs(form.getProductCategoryEntityId()));
        }

        if (form.getProductStatus() != null) {
            specification = specification.and(repository.productStatusIs(form.getProductStatus()));
        }

        return repository.findAll(specification, repository.defaultPageable(form));
    }
}
