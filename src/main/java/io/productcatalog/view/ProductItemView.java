package io.productcatalog.view;

import io.lib.view.BaseView;
import io.productcatalog.entity.ProductItem;
import io.productcatalog.entity.ProductStatus;

import java.math.BigDecimal;

public class ProductItemView extends BaseView<ProductItem>{
    public ProductItemView(ProductItem entity) {super(entity);}

    public String getName(){return entity.getName();}

    public String getDescription(){return entity.getDescription();}

    public BigDecimal getPrice(){return entity.getPrice();}

    public ProductStatus getProductStatus(){return entity.getProductStatus();}

    public String getImageUrl(){return entity.getImageUrl();}

    public String getProductCategoryEntityId(){return entity.getProductCategoryEntityId();}

    public String getProductCategoryName(){
        return entity.getProductCategory() == null
                ? null
                : entity.getProductCategory().getName();
    }
}
